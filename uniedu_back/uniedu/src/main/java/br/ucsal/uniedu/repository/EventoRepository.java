package br.ucsal.uniedu.repository;

import java.util.List;
import br.ucsal.uniedu.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findAllByOrderByDataEvento();

    List<Evento> findByTurmaCodTurmaOrderByDataEvento(Integer codTurma);

    List<Evento> findByProfessorCodProfOrderByDataEvento(Integer codProf);

    long countByTurmaCodTurma(Integer codTurma);

}
