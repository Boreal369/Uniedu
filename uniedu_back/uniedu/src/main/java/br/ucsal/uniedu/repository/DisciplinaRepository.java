package br.ucsal.uniedu.repository;

import br.ucsal.uniedu.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

}
