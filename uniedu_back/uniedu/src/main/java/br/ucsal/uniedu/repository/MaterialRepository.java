package br.ucsal.uniedu.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.ucsal.uniedu.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    List<Material> findAllByOrderByCodMaterialDesc();

    List<Material> findByProfessorCodProfOrderByCodMaterialDesc(Integer codProf);

    @Query("select m from Material m where m.disciplina.codDisciplina in "
         + "(select td.disciplina.codDisciplina from TurDisc td where td.turma.codTurma = :codTurma) "
         + "order by m.codMaterial desc")
    List<Material> findDaTurma(@Param("codTurma") Integer codTurma);

    boolean existsByDisciplinaCodDisciplina(Integer codDisciplina);

}
