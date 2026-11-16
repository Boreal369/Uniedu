package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.AlunoRequest;
import br.ucsal.uniedu.dto.AlunoResponse;
import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.dto.NotaResponse;
import br.ucsal.uniedu.service.AlunoService;
import br.ucsal.uniedu.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final NotaService notaService;

    public AlunoController(AlunoService alunoService, NotaService notaService) {
        this.alunoService = alunoService;
        this.notaService = notaService;
    }

    // RF001
    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@RequestBody AlunoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.cadastrar(request));
    }

    // RF003
    @PutMapping("/{codAluno}")
    public AlunoResponse atualizar(@PathVariable("codAluno") Integer codAluno,
                                   @RequestBody Dtos.AlunoUpdateRequest request) {
        return alunoService.atualizar(codAluno, request);
    }

    // RF007
    @GetMapping
    public List<AlunoResponse> listar(@RequestParam(value = "codTurma", required = false) Integer codTurma) {
        return alunoService.listar(codTurma);
    }

    // RF009
    @GetMapping("/{codAluno}/notas")
    public List<NotaResponse> notasDoAluno(@PathVariable("codAluno") Integer codAluno) {
        return notaService.listarPorAluno(codAluno);
    }
}
