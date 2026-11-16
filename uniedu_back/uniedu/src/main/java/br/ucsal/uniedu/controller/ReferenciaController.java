package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.model.Disciplina;
import br.ucsal.uniedu.model.Turma;
import br.ucsal.uniedu.repository.DisciplinaRepository;
import br.ucsal.uniedu.repository.TurmaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReferenciaController {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;

    public ReferenciaController(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    // RF005
    @GetMapping("/turmas")
    public List<Turma> turmas() { return turmaRepository.findAll(); }

    // RF006
    @GetMapping("/disciplinas")
    public List<Disciplina> disciplinas() { return disciplinaRepository.findAll(); }
}
