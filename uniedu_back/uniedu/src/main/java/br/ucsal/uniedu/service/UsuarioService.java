package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Frequencia;
import br.ucsal.uniedu.model.Nota;
import br.ucsal.uniedu.model.Usuario;
import br.ucsal.uniedu.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final double MEDIA = 6.0;

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final TurDiscRepository turDiscRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, AlunoRepository alunoRepository,
                          ProfessorRepository professorRepository, NotaRepository notaRepository,
                          FrequenciaRepository frequenciaRepository, TurDiscRepository turDiscRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.notaRepository = notaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.turDiscRepository = turDiscRepository;
    }

    // RF004
    @Transactional
    public void alterarAtivo(Integer codUsuario, Boolean ativo) {
        if (ativo == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe se o usuário deve ficar ativo ou inativo.");
        }
        Usuario u = usuarioRepository.findById(codUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if ("ADMIN".equals(u.getPerfil())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Não é possível desativar o administrador.");
        }
        u.setAtivo(ativo);
        usuarioRepository.save(u);
    }

    // RF007
    @Transactional(readOnly = true)
    public List<Dtos.AlunoDetalhe> listarAlunos() {
        Map<Integer, List<Nota>> notas = notaRepository.findAll().stream()
                .collect(Collectors.groupingBy(n -> n.getAluno().getCodAluno()));
        Map<Integer, List<Frequencia>> freqs = frequenciaRepository.findAll().stream()
                .collect(Collectors.groupingBy(f -> f.getAluno().getCodAluno()));
        return alunoRepository.findAll().stream().map(a -> {
            List<Nota> ns = notas.getOrDefault(a.getCodAluno(), List.of());
            List<Frequencia> fs = freqs.getOrDefault(a.getCodAluno(), List.of());
            Double media = ns.isEmpty() ? null
                    : Util.arredondar1(ns.stream().mapToDouble(n -> n.getValor().doubleValue()).average().orElse(0));
            long abaixo = ns.stream().filter(n -> n.getValor().doubleValue() < MEDIA).count();
            Double freq = fs.isEmpty() ? null
                    : Util.arredondar1(fs.stream().filter(f -> Boolean.TRUE.equals(f.getPresenca())).count() * 100.0 / fs.size());
            Usuario u = a.getUsuario();
            return new Dtos.AlunoDetalhe(a.getCodAluno(), u.getCodUsuario(), u.getNome(), u.getEmail(), u.getLogin(),
                    a.getMatricula(), a.getTurma().getCodTurma(), a.getTurma().getNomeTurma(), u.getAtivo(),
                    media, freq, abaixo);
        }).toList();
    }

    // RF007
    @Transactional(readOnly = true)
    public List<Dtos.ProfessorDetalhe> listarProfessores() {
        return professorRepository.findAll().stream().map(p -> {
            List<String> vinculos = turDiscRepository.findByProfessorCodProf(p.getCodProf()).stream()
                    .map(td -> td.getTurma().getNomeTurma() + " · " + td.getDisciplina().getNome()).toList();
            Usuario u = p.getUsuario();
            return new Dtos.ProfessorDetalhe(p.getCodProf(), u.getCodUsuario(), u.getNome(), u.getEmail(),
                    u.getLogin(), u.getAtivo(), vinculos);
        }).toList();
    }
}
