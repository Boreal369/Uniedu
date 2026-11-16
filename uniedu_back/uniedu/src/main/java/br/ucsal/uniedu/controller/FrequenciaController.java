package br.ucsal.uniedu.controller;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.service.FrequenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FrequenciaController {

    private final FrequenciaService frequenciaService;

    public FrequenciaController(FrequenciaService frequenciaService) { this.frequenciaService = frequenciaService; }

    // RF013
    @PostMapping("/frequencias/lote")
    public Map<String, Integer> registrar(@RequestBody Dtos.FrequenciaLoteRequest request) {
        return Map.of("registrados", frequenciaService.registrarLote(request));
    }

    // RF009
    @GetMapping("/alunos/{codAluno}/frequencia")
    public Dtos.FrequenciaResumo resumo(@PathVariable("codAluno") Integer codAluno) {
        return frequenciaService.resumo(codAluno);
    }
}
