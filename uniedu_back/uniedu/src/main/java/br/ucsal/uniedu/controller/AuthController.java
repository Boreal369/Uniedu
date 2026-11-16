package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.LoginRequest;
import br.ucsal.uniedu.dto.LoginResponse;
import br.ucsal.uniedu.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
