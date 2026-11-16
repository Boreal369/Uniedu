package br.ucsal.uniedu.repository;

import java.util.Optional;
import br.ucsal.uniedu.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    Optional<Professor> findByUsuarioCodUsuario(Integer codUsuario);
}