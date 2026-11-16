package br.ucsal.uniedu.repository;

import java.util.List;
import br.ucsal.uniedu.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    List<Documento> findByAlunoCodAlunoOrderByCodDocumentoDesc(Integer codAluno);

}
