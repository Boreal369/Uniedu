package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    // RF007
    @GetMapping("/alunos")
    public List<Dtos.AlunoDetalhe> alunos() { return usuarioService.listarAlunos(); }

    // RF007
    @GetMapping("/professores")
    public List<Dtos.ProfessorDetalhe> professores() { return usuarioService.listarProfessores(); }

    // RF004
    @PutMapping("/{codUsuario}/ativo")
    public ResponseEntity<Void> alterarAtivo(@PathVariable("codUsuario") Integer codUsuario,
                                             @RequestBody Dtos.AtivoRequest request) {
        usuarioService.alterarAtivo(codUsuario, request.ativo());
        return ResponseEntity.noContent().build();
    }
}
