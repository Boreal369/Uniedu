package br.ucsal.uniedu.service;

import br.ucsal.uniedu.dto.Dtos;
import br.ucsal.uniedu.exception.ApiException;
import br.ucsal.uniedu.model.Aluno;
import br.ucsal.uniedu.model.Disciplina;
import br.ucsal.uniedu.model.Frequencia;
import br.ucsal.uniedu.repository.AlunoRepository;
import br.ucsal.uniedu.repository.DisciplinaRepository;
import br.ucsal.uniedu.repository.FrequenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public FrequenciaService(FrequenciaRepository frequenciaRepository, AlunoRepository alunoRepository,
                             DisciplinaRepository disciplinaRepository) {
        this.frequenciaRepository = frequenciaRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    // RF013
    @Transactional
    public int registrarLote(Dtos.FrequenciaLoteRequest r) {
        if (r == null || r.codDisciplina() == null || r.data() == null
                || r.registros() == null || r.registros().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe disciplina, data e a lista de alunos.");
        }
        Disciplina d = disciplinaRepository.findById(r.codDisciplina())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disciplina não encontrada."));
        int registrados = 0;
        for (Dtos.RegistroFrequencia reg : r.registros()) {
            if (reg.codAluno() == null) continue;
            Aluno a = alunoRepository.findById(reg.codAluno())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluno não encontrado: " + reg.codAluno()));
            Frequencia f = frequenciaRepository
                    .findByAlunoCodAlunoAndDisciplinaCodDisciplinaAndDataAula(reg.codAluno(), r.codDisciplina(), r.data())
                    .orElseGet(Frequencia::new);
            f.setAluno(a);
            f.setDisciplina(d);
            f.setDataAula(r.data());
            f.setPresenca(Boolean.TRUE.equals(reg.presenca()));
            frequenciaRepository.save(f);
            registrados++;
        }
        return registrados;
    }

    // RF009
    @Transactional(readOnly = true)
    public Dtos.FrequenciaResumo resumo(Integer codAluno) {
        List<Frequencia> lista = frequenciaRepository.findByAlunoCodAluno(codAluno);
        long total = lista.size();
        long presentes = lista.stream().filter(f -> Boolean.TRUE.equals(f.getPresenca())).count();

        Map<Integer, long[]> contagem = new LinkedHashMap<>();
        Map<Integer, String> nomes = new LinkedHashMap<>();
        for (Frequencia f : lista) {
            Integer cod = f.getDisciplina().getCodDisciplina();
            nomes.put(cod, f.getDisciplina().getNome());
            long[] c = contagem.computeIfAbsent(cod, k -> new long[2]);
            c[0]++;
            if (Boolean.TRUE.equals(f.getPresenca())) c[1]++;
        }
        List<Dtos.FrequenciaPorDisciplina> porDisciplina = new ArrayList<>();
        for (Map.Entry<Integer, long[]> e : contagem.entrySet()) {
            long[] c = e.getValue();
            porDisciplina.add(new Dtos.FrequenciaPorDisciplina(e.getKey(), nomes.get(e.getKey()), c[0], c[1],
                    Util.arredondar1(c[1] * 100.0 / c[0])));
        }
        Double pct = total == 0 ? null : Util.arredondar1(presentes * 100.0 / total);
        return new Dtos.FrequenciaResumo(total, presentes, pct, porDisciplina);
    }
}