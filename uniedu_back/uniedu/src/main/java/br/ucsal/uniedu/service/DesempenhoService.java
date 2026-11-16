package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.BaixoRendimentoResponse;
import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Aluno;
import br.ucsal.uniedu.model.Turma;
import br.ucsal.uniedu.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DesempenhoService {

    private static final BigDecimal MEDIA = new BigDecimal("6.0");
    private static final long LIMITE = 3;

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final FrequenciaRepository frequenciaRepository;

    public DesempenhoService(NotaRepository notaRepository, AlunoRepository alunoRepository,
                             ProfessorRepository professorRepository, TurmaRepository turmaRepository,
                             FrequenciaRepository frequenciaRepository) {
        this.notaRepository = notaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.turmaRepository = turmaRepository;
        this.frequenciaRepository = frequenciaRepository;
    }

    
    // RF011
    @Transactional(readOnly = true)
    public List<BaixoRendimentoResponse> baixoRendimento(Integer unidade) {
        if (unidade == null || unidade <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe uma unidade válida.");
        }
        List<BaixoRendimentoResponse> resultado = new ArrayList<>();
        for (Object[] linha : notaRepository.contarAbaixoDaMedia(unidade, MEDIA, LIMITE)) {
            Integer codAluno = (Integer) linha[0];
            Long quantidade = (Long) linha[1];
            Aluno a = alunoRepository.findById(codAluno).orElseThrow();
            resultado.add(new BaixoRendimentoResponse(codAluno, a.getUsuario().getNome(),
                    a.getTurma().getNomeTurma(), quantidade));
        }
        return resultado;
    }

    
    // RF009
    @Transactional(readOnly = true)
    public Dtos.ResumoAdmin resumo(Integer unidade) {
        return new Dtos.ResumoAdmin(alunoRepository.count(), professorRepository.count(),
                turmaRepository.count(), baixoRendimento(unidade).size());
    }

    
    // RF009
    @Transactional(readOnly = true)
    public List<Dtos.TurmaDesempenho> porTurma() {
        Map<String, Double> medias = new HashMap<>();
        for (Object[] l : notaRepository.mediaPorTurma()) medias.put((String) l[0], (Double) l[1]);
        Map<String, Long> totais = new HashMap<>();
        for (Object[] l : frequenciaRepository.contarRegistrosPorTurma()) totais.put((String) l[0], (Long) l[1]);
        Map<String, Long> presencas = new HashMap<>();
        for (Object[] l : frequenciaRepository.contarPresencasPorTurma()) presencas.put((String) l[0], (Long) l[1]);

        List<Dtos.TurmaDesempenho> resultado = new ArrayList<>();
        for (Turma t : turmaRepository.findAll()) {
            String nome = t.getNomeTurma();
            Double media = medias.containsKey(nome) ? Util.arredondar1(medias.get(nome)) : null;
            long total = totais.getOrDefault(nome, 0L);
            Double freq = total == 0 ? null : Util.arredondar1(presencas.getOrDefault(nome, 0L) * 100.0 / total);
            resultado.add(new Dtos.TurmaDesempenho(nome, media, freq));
        }
        return resultado;
    }
}