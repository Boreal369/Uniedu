package br.ucsal.uniedu.repository;

import java.util.List;
import br.ucsal.uniedu.model.TurDisc;
import br.ucsal.uniedu.model.TurDiscId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurDiscRepository extends JpaRepository<TurDisc, TurDiscId> {

    List<TurDisc> findByProfessorCodProf(Integer codProf);

    boolean existsByTurmaCodTurma(Integer codTurma);

    boolean existsByDisciplinaCodDisciplina(Integer codDisciplina);

}