package br.ucsal.uniedu.config;

import br.ucsal.uniedu.model.Usuario;
import br.ucsal.uniedu.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public DadosIniciais(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Coordenação");
            u.setEmail("admin@uniedu.com");
            u.setLogin("admin");
            u.setSenha(encoder.encode("admin123"));
            u.setPerfil("ADMIN");
            u.setAtivo(true);
            usuarioRepository.save(u);
        }
    }
}
