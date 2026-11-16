package br.ucsal.uniedu.repository;

import java.util.Optional;
import br.ucsal.uniedu.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {

    boolean existsByAlunoCodAlunoAndDisciplinaCodDisciplinaAndUnidade(
            Integer codAluno, Integer codDisciplina, Integer unidade);

    List<Nota> findByAlunoCodAluno(Integer codAluno);

    
    @Query("select n.aluno.codAluno, count(n) from Nota n "
         + "where n.unidade = :unidade and n.valor < :media "
         + "group by n.aluno.codAluno "
         + "having count(n) > :limite")
    List<Object[]> contarAbaixoDaMedia(@Param("unidade") Integer unidade,
                                       @Param("media") BigDecimal media,
                                       @Param("limite") Long limite);

    Optional<Nota> findByAlunoCodAlunoAndDisciplinaCodDisciplinaAndUnidade(
            Integer codAluno, Integer codDisciplina, Integer unidade);

    @Query("select n from Nota n where n.aluno.turma.codTurma = :codTurma "
         + "and n.disciplina.codDisciplina = :codDisciplina and n.unidade = :unidade")
    List<Nota> findPorTurmaDisciplinaUnidade(@Param("codTurma") Integer codTurma,
                                             @Param("codDisciplina") Integer codDisciplina,
                                             @Param("unidade") Integer unidade);

    @Query("select t.nomeTurma, avg(n.valor) from Nota n join n.aluno a join a.turma t group by t.nomeTurma")
    List<Object[]> mediaPorTurma();

    boolean existsByDisciplinaCodDisciplina(Integer codDisciplina);

}
