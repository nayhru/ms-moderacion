package com.foro.moderacion.repository;

import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.model.Reporte;
import com.foro.moderacion.model.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findAllByOrderByFechaCreacionDesc();

    List<Reporte> findByEstadoOrderByFechaCreacionDesc(EstadoReporte estado);

    boolean existsByTipoContenidoAndIdContenidoAndUsuarioIdAndEstado(TipoContenido tipoContenido, Long idContenido,
                                                                      Long usuarioId, EstadoReporte estado);
}
