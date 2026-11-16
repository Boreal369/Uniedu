package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.*;
import br.ucsal.uniedu.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CadastroService {

    private final UsuarioRepository usuarioRepository;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final TurDiscRepository turDiscRepository;
    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final MaterialRepository materialRepository;
    private final PasswordEncoder encoder;

    public CadastroService(UsuarioRepository usuarioRepository, ProfessorRepository professorRepository,
                           TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository,
                           TurDiscRepository turDiscRepository, AlunoRepository alunoRepository,
                           NotaRepository notaRepository, FrequenciaRepository frequenciaRepository,
                           MaterialRepository materialRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.professorRepository = professorRepository;
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.turDiscRepository = turDiscRepository;
        this.alunoRepository = alunoRepository;
        this.notaRepository = notaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.materialRepository = materialRepository;
        this.encoder = encoder;
    }

    // RF002
    @Transactional
    public Dtos.ProfessorResponse cadastrarProfessor(Dtos.ProfessorRequest r) {
        if (r == null || Util.vazio(r.nome()) || Util.vazio(r.email()) || Util.vazio(r.login()) || Util.vazio(r.senha())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        if (usuarioRepository.findByLogin(r.login().trim()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse login.");
        }
        if (usuarioRepository.findByEmail(r.email().trim()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail.");
        }
        Usuario u = new Usuario();
        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim());
        u.setLogin(r.login().trim());
        u.setSenha(encoder.encode(r.senha()));
        u.setPerfil("PROFESSOR");
        u.setAtivo(true);
        usuarioRepository.save(u);

        Professor p = new Professor();
        p.setUsuario(u);
        professorRepository.save(p);
        return paraResposta(p);
    }

    // RF003
    @Transactional
    public Dtos.ProfessorResponse atualizarProfessor(Integer codProf, Dtos.ProfessorUpdateRequest r) {
        if (r == null || Util.vazio(r.nome()) || Util.vazio(r.email()) || Util.vazio(r.login())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        Professor p = professorRepository.findById(codProf)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professor não encontrado."));
        Usuario u = p.getUsuario();

        usuarioRepository.findByLogin(r.login().trim())
                .filter(x -> !x.getCodUsuario().equals(u.getCodUsuario()))
                .ifPresent(x -> { throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse login."); });
        usuarioRepository.findByEmail(r.email().trim())
                .filter(x -> !x.getCodUsuario().equals(u.getCodUsuario()))
                .ifPresent(x -> { throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail."); });

        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim());
        u.setLogin(r.login().trim());
        if (!Util.vazio(r.senha())) {
            u.setSenha(encoder.encode(r.senha()));
        }
        usuarioRepository.save(u);
        return paraResposta(p);
    }

    // RF007
    @Transactional(readOnly = true)
    public List<Dtos.ProfessorResponse> listarProfessores() {
        return professorRepository.findAll().stream().map(this::paraResposta).toList();
    }

    // RF005
    @Transactional
    public Turma cadastrarTurma(Dtos.TurmaRequest r) {
        if (r == null || Util.vazio(r.nomeTurma()) || r.anoLetivo() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o nome e o ano letivo da turma.");
        }
        if (r.anoLetivo() < 2000) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ano letivo inválido.");
        }
        if (turmaRepository.existsByNomeTurmaAndAnoLetivo(r.nomeTurma().trim(), r.anoLetivo())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe essa turma nesse ano letivo.");
        }
        Turma t = new Turma();
        t.setNomeTurma(r.nomeTurma().trim());
        t.setAnoLetivo(r.anoLetivo());
        return turmaRepository.save(t);
    }

    // RF005
    @Transactional
    public Turma atualizarTurma(Integer codTurma, Dtos.TurmaRequest r) {
        if (r == null || Util.vazio(r.nomeTurma()) || r.anoLetivo() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o nome e o ano letivo da turma.");
        }
        if (r.anoLetivo() < 2000) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ano letivo inválido.");
        }
        Turma t = turmaRepository.findById(codTurma)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        String nome = r.nomeTurma().trim();
        boolean mudou = !t.getNomeTurma().equals(nome) || !t.getAnoLetivo().equals(r.anoLetivo());
        if (mudou && turmaRepository.existsByNomeTurmaAndAnoLetivo(nome, r.anoLetivo())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe essa turma nesse ano letivo.");
        }
        t.setNomeTurma(nome);
        t.setAnoLetivo(r.anoLetivo());
        return turmaRepository.save(t);
    }

    // RF005
    @Transactional
    public void excluirTurma(Integer codTurma) {
        Turma t = turmaRepository.findById(codTurma)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        if (alunoRepository.countByTurmaCodTurma(codTurma) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "A turma possui alunos e não pode ser excluída.");
        }
        if (turDiscRepository.existsByTurmaCodTurma(codTurma)) {
            throw new ApiException(HttpStatus.CONFLICT, "A turma possui disciplinas vinculadas e não pode ser excluída.");
        }
        turmaRepository.delete(t);
    }

    // RF006
    @Transactional
    public Disciplina cadastrarDisciplina(Dtos.DisciplinaRequest r) {
        if (r == null || Util.vazio(r.nome())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o nome da disciplina.");
        }
        if (disciplinaRepository.existsByNomeIgnoreCase(r.nome().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe uma disciplina com esse nome.");
        }
        Disciplina d = new Disciplina();
        d.setNome(r.nome().trim());
        return disciplinaRepository.save(d);
    }

    // RF006
    @Transactional
    public Disciplina atualizarDisciplina(Integer codDisciplina, Dtos.DisciplinaRequest r) {
        if (r == null || Util.vazio(r.nome())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o nome da disciplina.");
        }
        Disciplina d = disciplinaRepository.findById(codDisciplina)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        String nome = r.nome().trim();
        if (!d.getNome().equalsIgnoreCase(nome) && disciplinaRepository.existsByNomeIgnoreCase(nome)) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe uma disciplina com esse nome.");
        }
        d.setNome(nome);
        return disciplinaRepository.save(d);
    }

    // RF006
    @Transactional
    public void excluirDisciplina(Integer codDisciplina) {
        Disciplina d = disciplinaRepository.findById(codDisciplina)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        if (turDiscRepository.existsByDisciplinaCodDisciplina(codDisciplina)
                || notaRepository.existsByDisciplinaCodDisciplina(codDisciplina)
                || frequenciaRepository.existsByDisciplinaCodDisciplina(codDisciplina)
                || materialRepository.existsByDisciplinaCodDisciplina(codDisciplina)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "A disciplina está em uso (vínculos, notas, frequência ou materiais) e não pode ser excluída.");
        }
        disciplinaRepository.delete(d);
    }

    // RF010
    @Transactional
    public Dtos.VinculoResponse vincular(Dtos.VinculoRequest r) {
        if (r == null || r.codTurma() == null || r.codDisciplina() == null || r.codProf() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Escolha professor, turma e disciplina.");
        }
        Turma turma = turmaRepository.findById(r.codTurma())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));
        Disciplina disc = disciplinaRepository.findById(r.codDisciplina())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        Professor prof = professorRepository.findById(r.codProf())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professor não encontrado."));

        TurDiscId id = new TurDiscId(r.codTurma(), r.codDisciplina());
        if (turDiscRepository.existsById(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Essa disciplina já está vinculada a essa turma.");
        }
        TurDisc td = new TurDisc();
        td.setId(id);
        td.setTurma(turma);
        td.setDisciplina(disc);
        td.setProfessor(prof);
        turDiscRepository.save(td);
        return paraResposta(td);
    }

    // RF010
    @Transactional(readOnly = true)
    public List<Dtos.VinculoResponse> listarVinculos(Integer codProf) {
        List<TurDisc> lista = (codProf == null) ? turDiscRepository.findAll() : turDiscRepository.findByProfessorCodProf(codProf);
        return lista.stream().map(this::paraResposta).toList();
    }

    private Dtos.ProfessorResponse paraResposta(Professor p) {
        return new Dtos.ProfessorResponse(p.getCodProf(), p.getUsuario().getNome(), p.getUsuario().getEmail());
    }

    private Dtos.VinculoResponse paraResposta(TurDisc td) {
        long qtd = alunoRepository.countByTurmaCodTurma(td.getTurma().getCodTurma());
        return new Dtos.VinculoResponse(td.getTurma().getCodTurma(), td.getTurma().getNomeTurma(),
                td.getDisciplina().getCodDisciplina(), td.getDisciplina().getNome(),
                td.getProfessor().getCodProf(), td.getProfessor().getUsuario().getNome(), qtd);
    }
}