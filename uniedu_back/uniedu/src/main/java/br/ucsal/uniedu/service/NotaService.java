package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.NotaRequest;
import br.ucsal.uniedu.dto.NotaResponse;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Aluno;
import br.ucsal.uniedu.model.Disciplina;
import br.ucsal.uniedu.model.Nota;
import br.ucsal.uniedu.repository.AlunoRepository;
import br.ucsal.uniedu.repository.DisciplinaRepository;
import br.ucsal.uniedu.repository.NotaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public NotaService(NotaRepository notaRepository, AlunoRepository alunoRepository,
                       DisciplinaRepository disciplinaRepository) {
        this.notaRepository = notaRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    // RF012
    @Transactional
    public NotaResponse lancar(NotaRequest r) {
        validar(r);
        if (notaRepository.existsByAlunoCodAlunoAndDisciplinaCodDisciplinaAndUnidade(
                r.codAluno(), r.codDisciplina(), r.unidade())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe nota desse aluno nessa disciplina e unidade.");
        }
        return criar(r);
    }

    // RF012
    @Transactional
    public NotaResponse salvar(NotaRequest r) {
        validar(r);
        Optional<Nota> existente = notaRepository.findByAlunoCodAlunoAndDisciplinaCodDisciplinaAndUnidade(
                r.codAluno(), r.codDisciplina(), r.unidade());
        if (existente.isPresent()) {
            Nota n = existente.get();
            n.setValor(r.valor().setScale(2, RoundingMode.HALF_UP));
            n.setDataLancamento(LocalDate.now());
            notaRepository.save(n);
            return paraResposta(n);
        }
        return criar(r);
    }

    // RF009
    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorAluno(Integer codAluno) {
        return notaRepository.findByAlunoCodAluno(codAluno).stream().map(this::paraResposta).toList();
    }

    // RF009
    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorTurma(Integer codTurma, Integer codDisciplina, Integer unidade) {
        if (codTurma == null || codDisciplina == null || unidade == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe turma, disciplina e unidade.");
        }
        return notaRepository.findPorTurmaDisciplinaUnidade(codTurma, codDisciplina, unidade)
                .stream().map(this::paraResposta).toList();
    }

    private void validar(NotaRequest r) {
        if (r == null || r.codAluno() == null || r.codDisciplina() == null
                || r.unidade() == null || r.valor() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        if (r.unidade() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A unidade deve ser maior que zero.");
        }
        if (r.valor().compareTo(BigDecimal.ZERO) < 0 || r.valor().compareTo(BigDecimal.TEN) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A nota deve estar entre 0 e 10.");
        }
    }

    private NotaResponse criar(NotaRequest r) {
        Aluno aluno = alunoRepository.findById(r.codAluno())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        Disciplina disc = disciplinaRepository.findById(r.codDisciplina())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        Nota n = new Nota();
        n.setAluno(aluno);
        n.setDisciplina(disc);
        n.setUnidade(r.unidade());
        n.setValor(r.valor().setScale(2, RoundingMode.HALF_UP));
        notaRepository.save(n);
        return paraResposta(n);
    }

    private NotaResponse paraResposta(Nota n) {
        return new NotaResponse(n.getCodNota(), n.getAluno().getCodAluno(),
                n.getAluno().getUsuario().getNome(), n.getDisciplina().getCodDisciplina(),
                n.getDisciplina().getNome(), n.getUnidade(), n.getValor(), n.getDataLancamento());
    }
}