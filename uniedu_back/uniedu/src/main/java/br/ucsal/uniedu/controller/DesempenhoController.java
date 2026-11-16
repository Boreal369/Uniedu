package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.BaixoRendimentoResponse;
import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.service.DesempenhoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/desempenho")
public class DesempenhoController {

    private final DesempenhoService desempenhoService;

    public DesempenhoController(DesempenhoService desempenhoService) { this.desempenhoService = desempenhoService; }

    // RF011
    @GetMapping("/baixo-rendimento")
    public List<BaixoRendimentoResponse> baixoRendimento(@RequestParam("unidade") Integer unidade) {
        return desempenhoService.baixoRendimento(unidade);
    }

    // RF009
    @GetMapping("/resumo")
    public Dtos.ResumoAdmin resumo(@RequestParam("unidade") Integer unidade) {
        return desempenhoService.resumo(unidade);
    }

    // RF009
    @GetMapping("/turmas")
    public List<Dtos.TurmaDesempenho> porTurma() {
        return desempenhoService.porTurma();
    }
}
