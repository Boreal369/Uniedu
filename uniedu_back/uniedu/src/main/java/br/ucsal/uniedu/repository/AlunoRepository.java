package br.ucsal.uniedu.repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ucsal.uniedu.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    boolean existsByMatricula(Long matricula);

    Optional<Aluno> findByUsuarioCodUsuario(Integer codUsuario);

    List<Aluno> findByTurmaCodTurma(Integer codTurma);

    long countByTurmaCodTurma(Integer codTurma);

    Optional<Aluno> findByMatricula(Long matricula);

}