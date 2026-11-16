package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final Pattern URL_BANCO = Pattern.compile("jdbc:postgresql://([^:/]+)(?::(\\d+))?/([^?]+).*");

    private final String url;
    private final String usuario;
    private final String senha;
    private final String pgDump;
    private final Path pasta;
    private final int manterDias;

    public BackupService(@Value("${spring.datasource.url}") String url,
                         @Value("${spring.datasource.username}") String usuario,
                         @Value("${spring.datasource.password:}") String senha,
                         @Value("${uniedu.backup.pg-dump:pg_dump}") String pgDump,
                         @Value("${uniedu.backup.dir:backups}") String pasta,
                         @Value("${uniedu.backup.manter-dias:30}") int manterDias) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
        this.pgDump = pgDump;
        this.pasta = Paths.get(pasta);
        this.manterDias = manterDias;
    }

    // RNF005
    @Scheduled(cron = "${uniedu.backup.cron:0 0 2 * * *}")
    public void backupDiario() {
        try {
            Dtos.BackupInfo info = executar();
            log.info("Backup diário concluído: {}", info.nome());
        } catch (Exception e) {
            log.error("Falha no backup diário: {}", e.getMessage());
        }
    }

    // RNF005
    public synchronized Dtos.BackupInfo executar() {
        Matcher m = URL_BANCO.matcher(url);
        if (!m.matches()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "URL do banco não reconhecida para o backup.");
        }
        String host = m.group(1);
        String porta = m.group(2) == null ? "5432" : m.group(2);
        String banco = m.group(3);
        try {
            Files.createDirectories(pasta);
            String nome = "uniedu_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".sql";
            Path arquivo = pasta.resolve(nome);

            ProcessBuilder pb = new ProcessBuilder(pgDump, "-h", host, "-p", porta, "-U", usuario,
                    "-F", "p", "-f", arquivo.toString(), banco);
            pb.environment().put("PGPASSWORD", senha);
            pb.redirectErrorStream(true);
            Process processo = pb.start();
            String saida = new String(processo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int codigo = processo.waitFor();
            if (codigo != 0) {
                Files.deleteIfExists(arquivo);
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "O pg_dump falhou: " + saida.trim());
            }
            limparAntigos();
            return info(arquivo);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível executar o backup. O pg_dump está instalado e configurado? " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Backup interrompido.");
        }
    }

    // RNF005
    public List<Dtos.BackupInfo> listar() {
        if (!Files.isDirectory(pasta)) {
            return List.of();
        }
        try (Stream<Path> arquivos = Files.list(pasta)) {
            return arquivos.filter(p -> p.toString().endsWith(".sql"))
                    .sorted(Comparator.comparing((Path p) -> p.getFileName().toString()).reversed())
                    .map(this::info).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private void limparAntigos() {
        Instant limite = Instant.now().minus(Duration.ofDays(manterDias));
        try (Stream<Path> arquivos = Files.list(pasta)) {
            arquivos.filter(p -> p.toString().endsWith(".sql")).forEach(p -> {
                try {
                    if (Files.getLastModifiedTime(p).toInstant().isBefore(limite)) {
                        Files.deleteIfExists(p);
                    }
                } catch (IOException e) {
                    log.warn("Não foi possível apagar o backup antigo {}", p.getFileName());
                }
            });
        } catch (IOException e) {
            log.warn("Não foi possível limpar backups antigos: {}", e.getMessage());
        }
    }

    private Dtos.BackupInfo info(Path arquivo) {
        long kb = 0;
        String data = "";
        try {
            kb = Math.max(1, Files.size(arquivo) / 1024);
            data = LocalDateTime.ofInstant(Files.getLastModifiedTime(arquivo).toInstant(), ZoneId.systemDefault()).toString();
        } catch (IOException e) {
            log.warn("Não foi possível ler o backup {}", arquivo.getFileName());
        }
        return new Dtos.BackupInfo(arquivo.getFileName().toString(), kb, data);
    }
}
