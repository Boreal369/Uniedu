package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.NotaRequest;
import br.ucsal.uniedu.dto.NotaResponse;
import br.ucsal.uniedu.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) { this.notaService = notaService; }

    // RF012
    @PostMapping
    public ResponseEntity<NotaResponse> lancar(@RequestBody NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.lancar(request));
    }

    // RF012
    @PutMapping
    public NotaResponse salvar(@RequestBody NotaRequest request) {
        return notaService.salvar(request);
    }

    // RF009
    @GetMapping
    public List<NotaResponse> listar(@RequestParam("codTurma") Integer codTurma,
                                     @RequestParam("codDisciplina") Integer codDisciplina,
                                     @RequestParam("unidade") Integer unidade) {
        return notaService.listarPorTurma(codTurma, codDisciplina, unidade);
    }
}
