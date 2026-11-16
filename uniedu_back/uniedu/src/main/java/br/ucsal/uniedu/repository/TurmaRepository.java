package br.ucsal.uniedu.repository;

import br.ucsal.uniedu.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Integer> {

    boolean existsByNomeTurmaAndAnoLetivo(String nomeTurma, Integer anoLetivo);

}