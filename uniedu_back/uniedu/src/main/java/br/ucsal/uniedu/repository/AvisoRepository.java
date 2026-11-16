package br.ucsal.uniedu.repository;

import java.util.List;
import br.ucsal.uniedu.model.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, Integer> {
    List<Aviso> findAllByOrderByCodAvisoDesc();
}
