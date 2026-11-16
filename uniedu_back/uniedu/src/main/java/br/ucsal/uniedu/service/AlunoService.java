package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.AlunoRequest;
import br.ucsal.uniedu.dto.AlunoResponse;
import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Aluno;
import br.ucsal.uniedu.model.Turma;
import br.ucsal.uniedu.model.Usuario;
import br.ucsal.uniedu.repository.AlunoRepository;
import br.ucsal.uniedu.repository.TurmaRepository;
import br.ucsal.uniedu.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;
    private final PasswordEncoder encoder;

    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository,
                        TurmaRepository turmaRepository, PasswordEncoder encoder) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.turmaRepository = turmaRepository;
        this.encoder = encoder;
    }

    // RF001
    @Transactional
    public AlunoResponse cadastrar(AlunoRequest r) {
        if (r == null || Util.vazio(r.nome()) || Util.vazio(r.email()) || Util.vazio(r.login())
                || Util.vazio(r.senha()) || r.matricula() == null || r.codTurma() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        if (usuarioRepository.findByLogin(r.login().trim()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse login.");
        }
        if (usuarioRepository.findByEmail(r.email().trim()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail.");
        }
        if (alunoRepository.existsByMatricula(r.matricula())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um aluno com essa matrícula.");
        }
        Turma turma = turmaRepository.findById(r.codTurma())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        Usuario u = new Usuario();
        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim());
        u.setLogin(r.login().trim());
        u.setSenha(encoder.encode(r.senha()));
        u.setPerfil("ALUNO");
        u.setAtivo(true);
        usuarioRepository.save(u);

        Aluno a = new Aluno();
        a.setMatricula(r.matricula());
        a.setTurma(turma);
        a.setUsuario(u);
        alunoRepository.save(a);

        return paraResposta(a);
    }

    // RF003
    @Transactional
    public AlunoResponse atualizar(Integer codAluno, Dtos.AlunoUpdateRequest r) {
        if (r == null || Util.vazio(r.nome()) || Util.vazio(r.email()) || Util.vazio(r.login())
                || r.matricula() == null || r.codTurma() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preencha todos os campos.");
        }
        Aluno a = alunoRepository.findById(codAluno)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        Usuario u = a.getUsuario();

        usuarioRepository.findByLogin(r.login().trim())
                .filter(x -> !x.getCodUsuario().equals(u.getCodUsuario()))
                .ifPresent(x -> { throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse login."); });
        usuarioRepository.findByEmail(r.email().trim())
                .filter(x -> !x.getCodUsuario().equals(u.getCodUsuario()))
                .ifPresent(x -> { throw new ApiException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail."); });
        alunoRepository.findByMatricula(r.matricula())
                .filter(x -> !x.getCodAluno().equals(a.getCodAluno()))
                .ifPresent(x -> { throw new ApiException(HttpStatus.CONFLICT, "Já existe um aluno com essa matrícula."); });
        Turma turma = turmaRepository.findById(r.codTurma())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Turma não encontrada."));

        u.setNome(r.nome().trim());
        u.setEmail(r.email().trim());
        u.setLogin(r.login().trim());
        if (!Util.vazio(r.senha())) {
            u.setSenha(encoder.encode(r.senha()));
        }
        a.setMatricula(r.matricula());
        a.setTurma(turma);
        usuarioRepository.save(u);
        alunoRepository.save(a);
        return paraResposta(a);
    }

    // RF007
    @Transactional(readOnly = true)
    public List<AlunoResponse> listar(Integer codTurma) {
        List<Aluno> lista = (codTurma == null) ? alunoRepository.findAll() : alunoRepository.findByTurmaCodTurma(codTurma);
        return lista.stream().map(this::paraResposta).toList();
    }

    private AlunoResponse paraResposta(Aluno a) {
        Usuario u = a.getUsuario();
        return new AlunoResponse(a.getCodAluno(), u.getNome(), u.getEmail(), u.getLogin(),
                a.getMatricula(), a.getTurma().getCodTurma(), a.getTurma().getNomeTurma());
    }
}