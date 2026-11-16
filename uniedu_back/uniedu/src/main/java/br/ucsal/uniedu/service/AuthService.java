package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.LoginRequest;
import br.ucsal.uniedu.dto.LoginResponse;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Aluno;
import br.ucsal.uniedu.model.Professor;
import br.ucsal.uniedu.model.Usuario;
import br.ucsal.uniedu.repository.AlunoRepository;
import br.ucsal.uniedu.repository.ProfessorRepository;
import br.ucsal.uniedu.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder encoder;

    public AuthService(UsuarioRepository usuarioRepository, AlunoRepository alunoRepository,
                       ProfessorRepository professorRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        if (req == null || Util.vazio(req.login()) || Util.vazio(req.senha())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe login e senha.");
        }

        Usuario u = usuarioRepository.findByLogin(req.login().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Login ou senha inválidos."));

        if (!encoder.matches(req.senha(), u.getSenha())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Login ou senha inválidos.");
        }
        if (!Boolean.TRUE.equals(u.getAtivo())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Usuário inativo. Procure a secretaria.");
        }

        Integer codAluno = null, codProf = null, codTurma = null;
        if ("ALUNO".equals(u.getPerfil())) {
            Aluno a = alunoRepository.findByUsuarioCodUsuario(u.getCodUsuario()).orElse(null);
            if (a != null) {
                codAluno = a.getCodAluno();
                codTurma = a.getTurma().getCodTurma();
            }
        } else if ("PROFESSOR".equals(u.getPerfil())) {
            Professor p = professorRepository.findByUsuarioCodUsuario(u.getCodUsuario()).orElse(null);
            if (p != null) codProf = p.getCodProf();
        }
        return new LoginResponse(u.getCodUsuario(), u.getNome(), u.getPerfil(), codAluno, codProf, codTurma);
    }
}
