package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.model.Disciplina;
import br.ucsal.uniedu.model.Turma;
import br.ucsal.uniedu.service.CadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) { this.cadastroService = cadastroService; }

    // RF002
    @PostMapping("/professores")
    public ResponseEntity<Dtos.ProfessorResponse> cadastrarProfessor(@RequestBody Dtos.ProfessorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroService.cadastrarProfessor(request));
    }

    // RF003
    @PutMapping("/professores/{codProf}")
    public Dtos.ProfessorResponse atualizarProfessor(@PathVariable("codProf") Integer codProf,
                                                     @RequestBody Dtos.ProfessorUpdateRequest request) {
        return cadastroService.atualizarProfessor(codProf, request);
    }

    // RF007
    @GetMapping("/professores")
    public List<Dtos.ProfessorResponse> listarProfessores() { return cadastroService.listarProfessores(); }

    // RF005
    @PostMapping("/turmas")
    public ResponseEntity<Turma> cadastrarTurma(@RequestBody Dtos.TurmaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroService.cadastrarTurma(request));
    }

    // RF005
    @PutMapping("/turmas/{codTurma}")
    public Turma atualizarTurma(@PathVariable("codTurma") Integer codTurma, @RequestBody Dtos.TurmaRequest request) {
        return cadastroService.atualizarTurma(codTurma, request);
    }

    // RF005
    @DeleteMapping("/turmas/{codTurma}")
    public ResponseEntity<Void> excluirTurma(@PathVariable("codTurma") Integer codTurma) {
        cadastroService.excluirTurma(codTurma);
        return ResponseEntity.noContent().build();
    }

    // RF006
    @PostMapping("/disciplinas")
    public ResponseEntity<Disciplina> cadastrarDisciplina(@RequestBody Dtos.DisciplinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroService.cadastrarDisciplina(request));
    }

    // RF006
    @PutMapping("/disciplinas/{codDisciplina}")
    public Disciplina atualizarDisciplina(@PathVariable("codDisciplina") Integer codDisciplina,
                                          @RequestBody Dtos.DisciplinaRequest request) {
        return cadastroService.atualizarDisciplina(codDisciplina, request);
    }

    // RF006
    @DeleteMapping("/disciplinas/{codDisciplina}")
    public ResponseEntity<Void> excluirDisciplina(@PathVariable("codDisciplina") Integer codDisciplina) {
        cadastroService.excluirDisciplina(codDisciplina);
        return ResponseEntity.noContent().build();
    }

    // RF010
    @PostMapping("/vinculos")
    public ResponseEntity<Dtos.VinculoResponse> vincular(@RequestBody Dtos.VinculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroService.vincular(request));
    }

    // RF010
    @GetMapping("/vinculos")
    public List<Dtos.VinculoResponse> listarVinculos(@RequestParam(value = "codProf", required = false) Integer codProf) {
        return cadastroService.listarVinculos(codProf);
    }
}
