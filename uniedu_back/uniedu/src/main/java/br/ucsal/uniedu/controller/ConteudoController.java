package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.service.ConteudoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConteudoController {

    private final ConteudoService conteudoService;

    public ConteudoController(ConteudoService conteudoService) { this.conteudoService = conteudoService; }

    // RF008
    @PostMapping("/avisos")
    public ResponseEntity<Dtos.AvisoResponse> criarAviso(@RequestBody Dtos.AvisoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoService.criarAviso(request));
    }

    // RF008
    @GetMapping("/avisos")
    public List<Dtos.AvisoResponse> listarAvisos(@RequestParam(value = "perfil", required = false) String perfil,
                                                 @RequestParam(value = "codTurma", required = false) Integer codTurma,
                                                 @RequestParam(value = "codUsuario", required = false) Integer codUsuario) {
        return conteudoService.listarAvisos(perfil, codTurma, codUsuario);
    }

    // RF014
    @PostMapping("/materiais")
    public ResponseEntity<Dtos.MaterialResponse> criarMaterial(@RequestBody Dtos.MaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoService.criarMaterial(request));
    }

    // RF014
    @GetMapping("/materiais")
    public List<Dtos.MaterialResponse> listarMateriais(@RequestParam(value = "codProf", required = false) Integer codProf,
                                                       @RequestParam(value = "codTurma", required = false) Integer codTurma) {
        return conteudoService.listarMateriais(codProf, codTurma);
    }

    // RF015
    @PostMapping("/eventos")
    public ResponseEntity<Dtos.EventoResponse> criarEvento(@RequestBody Dtos.EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoService.criarEvento(request));
    }

    // RF015
    @GetMapping("/eventos")
    public List<Dtos.EventoResponse> listarEventos(@RequestParam(value = "codProf", required = false) Integer codProf,
                                                   @RequestParam(value = "codTurma", required = false) Integer codTurma) {
        return conteudoService.listarEventos(codProf, codTurma);
    }

    @PostMapping("/documentos")
    public ResponseEntity<Dtos.DocumentoResponse> criarDocumento(@RequestBody Dtos.DocumentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conteudoService.criarDocumento(request));
    }

    @GetMapping("/alunos/{codAluno}/documentos")
    public List<Dtos.DocumentoResponse> documentosDoAluno(@PathVariable("codAluno") Integer codAluno) {
        return conteudoService.listarDocumentos(codAluno);
    }
}
