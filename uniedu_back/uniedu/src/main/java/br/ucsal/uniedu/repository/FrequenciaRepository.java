package br.ucsal.uniedu.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import br.ucsal.uniedu.model.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, Integer> {

    List<Frequencia> findByAlunoCodAluno(Integer codAluno);

    Optional<Frequencia> findByAlunoCodAlunoAndDisciplinaCodDisciplinaAndDataAula(
            Integer codAluno, Integer codDisciplina, java.time.LocalDate dataAula);

    @Query("select t.nomeTurma, count(f) from Frequencia f join f.aluno a join a.turma t group by t.nomeTurma")
    List<Object[]> contarRegistrosPorTurma();

    @Query("select t.nomeTurma, count(f) from Frequencia f join f.aluno a join a.turma t "
         + "where f.presenca = true group by t.nomeTurma")
    List<Object[]> contarPresencasPorTurma();

    boolean existsByDisciplinaCodDisciplina(Integer codDisciplina);

}
