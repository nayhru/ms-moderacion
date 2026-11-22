package com.foro.moderacion.service;

import com.foro.moderacion.dto.ReporteEstadoRequest;
import com.foro.moderacion.dto.ReporteRequest;
import com.foro.moderacion.dto.ReporteResponse;
import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.exception.RecursoNoEncontradoException;
import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.model.Reporte;
import com.foro.moderacion.repository.ReporteRepository;
import com.foro.moderacion.repository.SancionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private SancionRepository sancionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportes(EstadoReporte estado) {
        List<Reporte> reportes = estado == null
                ? reporteRepository.findAllByOrderByFechaCreacionDesc()
                : reporteRepository.findByEstadoOrderByFechaCreacionDesc(estado);
        log.debug("Listado de reportes: estado={}, resultados={}", estado, reportes.size());
        return reportes.stream().map(ReporteResponse::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteResponse getReporteById(Long id) {
        return ReporteResponse.desde(buscarReporte(id));
    }

    @Override
    @Transactional
    public ReporteResponse createReporte(ReporteRequest datos, Jwt usuario) {
        Long usuarioId = Permisos.idUsuario(usuario);
        if (reporteRepository.existsByTipoContenidoAndIdContenidoAndUsuarioIdAndEstado(
                datos.tipoContenido(), datos.idContenido(), usuarioId, EstadoReporte.PENDIENTE)) {
            throw new ConflictoException("Ya tienes un reporte pendiente sobre el " + datos.tipoContenido()
                    + " " + datos.idContenido());
        }
        Reporte reporte = new Reporte();
        reporte.setTipoContenido(datos.tipoContenido());
        reporte.setIdContenido(datos.idContenido());
        reporte.setMotivo(datos.motivo());
        reporte.setUsuarioId(usuarioId);
        reporte.setAutorReporte(usuario.getSubject());
        Reporte guardado = reporteRepository.save(reporte);
        log.info("Reporte creado: id={}, contenido={} {}, por={}", guardado.getId(), guardado.getTipoContenido(),
                guardado.getIdContenido(), guardado.getAutorReporte());
        return ReporteResponse.desde(guardado);
    }

    @Override
    @Transactional
    public ReporteResponse updateEstado(Long id, ReporteEstadoRequest datos, Jwt usuario) {
        Reporte reporte = buscarReporte(id);
        EstadoReporte anterior = reporte.getEstado();
        // Un reporte que origino una sancion vigente no puede reabrirse ni descartarse
        if (datos.estado() != EstadoReporte.RESUELTO && sancionRepository.existsByReporteIdAndActivaTrue(id)) {
            throw new ConflictoException("El reporte " + id + " tiene una sancion activa; revocala antes de cambiarlo a "
                    + datos.estado());
        }
        reporte.setEstado(datos.estado());
        log.info("Reporte {} cambia de {} a {}, por={}", id, anterior, datos.estado(), usuario.getSubject());
        return ReporteResponse.desde(reporte);
    }

    @Override
    @Transactional
    public void deleteReporte(Long id, Jwt usuario) {
        Reporte reporte = buscarReporte(id);
        if (sancionRepository.existsByReporteId(id)) {
            throw new ConflictoException("No se puede eliminar el reporte " + id + " porque tiene sanciones asociadas");
        }
        reporteRepository.delete(reporte);
        log.info("Reporte eliminado: id={}, contenido={} {}, por={}", id, reporte.getTipoContenido(),
                reporte.getIdContenido(), usuario.getSubject());
    }

    private Reporte buscarReporte(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reporte con id " + id));
    }
}
