package com.foro.moderacion.repository;

import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    // Cada filtro es opcional: si llega vacio no se aplica
    @Query("""
            SELECT s FROM Sancion s
            WHERE (:tipoContenido IS NULL OR s.tipoContenido = :tipoContenido)
              AND (:tipoSancion IS NULL OR s.tipoSancion = :tipoSancion)
              AND (:activa IS NULL OR s.activa = :activa)
            ORDER BY s.fechaCreacion DESC
            """)
    List<Sancion> buscar(@Param("tipoContenido") TipoContenido tipoContenido,
                         @Param("tipoSancion") TipoSancion tipoSancion,
                         @Param("activa") Boolean activa);

    List<Sancion> findByTipoContenidoAndIdContenidoAndActivaTrueOrderByFechaCreacionDesc(TipoContenido tipoContenido,
                                                                                         Long idContenido);

    // Busca otra sancion activa sobre el mismo contenido; excluirId evita que una sancion se encuentre a si misma al editarla
    @Query("""
            SELECT COUNT(s) > 0 FROM Sancion s
            WHERE s.tipoContenido = :tipoContenido AND s.idContenido = :idContenido
              AND s.tipoSancion IN :tipos AND s.activa = true
              AND (:excluirId IS NULL OR s.id <> :excluirId)
            """)
    boolean existeActiva(@Param("tipoContenido") TipoContenido tipoContenido, @Param("idContenido") Long idContenido,
                         @Param("tipos") Collection<TipoSancion> tipos, @Param("excluirId") Long excluirId);

    boolean existsByReporteId(Long reporteId);

    boolean existsByReporteIdAndActivaTrue(Long reporteId);
}
