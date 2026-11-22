package com.foro.moderacion.service;

import com.foro.moderacion.dto.EstadoContenidoResponse;
import com.foro.moderacion.dto.SancionRequest;
import com.foro.moderacion.dto.SancionResponse;
import com.foro.moderacion.dto.SancionUpdateRequest;
import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.exception.RecursoNoEncontradoException;
import com.foro.moderacion.exception.SolicitudInvalidaException;
import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.model.Reporte;
import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import com.foro.moderacion.repository.ReporteRepository;
import com.foro.moderacion.repository.SancionRepository;
import com.foro.moderacion.service.estrategia.SancionFactory;
import com.foro.moderacion.service.estrategia.SancionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SancionServiceImpl implements SancionService {

    @Autowired
    private SancionRepository sancionRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private SancionFactory sancionFactory;

    @Override
    @Transactional(readOnly = true)
    public List<SancionResponse> getSanciones(TipoContenido tipoContenido, TipoSancion tipoSancion, Boolean activa) {
        List<SancionResponse> sanciones = sancionRepository.buscar(tipoContenido, tipoSancion, activa)
                .stream().map(SancionResponse::desde).toList();
        log.debug("Listado de sanciones: tipoContenido={}, tipoSancion={}, activa={}, resultados={}",
                tipoContenido, tipoSancion, activa, sanciones.size());
        return sanciones;
    }

    @Override
    @Transactional(readOnly = true)
    public SancionResponse getSancionById(Long id) {
        return SancionResponse.desde(buscarSancion(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoContenidoResponse getEstadoContenido(TipoContenido tipoContenido, Long idContenido) {
        List<Sancion> activas = sancionRepository
                .findByTipoContenidoAndIdContenidoAndActivaTrueOrderByFechaCreacionDesc(tipoContenido, idContenido);
        // Cada estrategia sabe si su tipo de sancion oculta el contenido
        boolean baneado = activas.stream()
                .anyMatch(sancion -> sancionFactory.obtener(sancion.getTipoSancion()).bloqueaContenido());
        log.debug("Consulta de baneo: {} {}, sancionesActivas={}, baneado={}", tipoContenido, idContenido,
                activas.size(), baneado);
        return EstadoContenidoResponse.builder()
                .tipoContenido(tipoContenido)
                .idContenido(idContenido)
                .baneado(baneado)
                .sancionesActivas(activas.stream().map(SancionResponse::desde).toList())
                .build();
    }

    @Override
    @Transactional
    public SancionResponse createSancion(SancionRequest datos, Jwt moderador) {
        Reporte reporte = datos.reporteId() == null ? null : validarReporte(datos);
        validarSinDuplicado(datos.tipoContenido(), datos.idContenido(), datos.tipoSancion(), null);

        Sancion sancion = new Sancion();
        sancion.setTipoContenido(datos.tipoContenido());
        sancion.setIdContenido(datos.idContenido());
        sancion.setTipoSancion(datos.tipoSancion());
        sancion.setMotivo(datos.motivo());
        sancion.setModerador(moderador.getSubject());
        sancion.setReporte(reporte);

        SancionStrategy estrategia = sancionFactory.obtener(datos.tipoSancion());
        estrategia.aplicar(sancion, moderador);
        Sancion guardada = sancionRepository.save(sancion);
        log.info("Sancion creada: id={}, tipo={}, contenido={} {}, moderador={}", guardada.getId(),
                guardada.getTipoSancion(), guardada.getTipoContenido(), guardada.getIdContenido(), guardada.getModerador());

        if (reporte != null) {
            reporte.setEstado(EstadoReporte.RESUELTO);
            log.info("Reporte {} marcado como RESUELTO por la sancion {}", reporte.getId(), guardada.getId());
        }
        return SancionResponse.desde(guardada);
    }

    @Override
    @Transactional
    public SancionResponse updateSancion(Long id, SancionUpdateRequest datos, Jwt moderador) {
        Sancion sancion = buscarSancion(id);
        if (!sancion.isActiva()) {
            throw new ConflictoException("La sancion " + id + " esta revocada y ya no se puede editar");
        }
        TipoSancion anterior = sancion.getTipoSancion();
        if (anterior != datos.tipoSancion()) {
            validarSinDuplicado(sancion.getTipoContenido(), sancion.getIdContenido(), datos.tipoSancion(), id);
            sancion.setTipoSancion(datos.tipoSancion());
            // Al cambiar el tipo se vuelve a aplicar la regla de la nueva estrategia
            sancionFactory.obtener(datos.tipoSancion()).aplicar(sancion, moderador);
        }
        sancion.setMotivo(datos.motivo());
        log.info("Sancion actualizada: id={}, tipo {} -> {}, por={}", id, anterior, datos.tipoSancion(),
                moderador.getSubject());
        return SancionResponse.desde(sancion);
    }

    @Override
    @Transactional
    public SancionResponse revocarSancion(Long id, Jwt moderador) {
        Sancion sancion = buscarSancion(id);
        if (!sancion.isActiva()) {
            throw new ConflictoException("La sancion " + id + " ya estaba revocada");
        }
        sancion.setActiva(false);
        sancion.setFechaRevocacion(LocalDateTime.now());
        log.info("Sancion {} revocada: se quita el {} sobre el {} {}, por={}", id, sancion.getTipoSancion(),
                sancion.getTipoContenido(), sancion.getIdContenido(), moderador.getSubject());
        return SancionResponse.desde(sancion);
    }

    @Override
    @Transactional
    public void deleteSancion(Long id, Jwt moderador) {
        Sancion sancion = buscarSancion(id);
        sancionRepository.delete(sancion);
        log.info("Sancion eliminada: id={}, tipo={}, contenido={} {}, activa={}, por={}", id, sancion.getTipoSancion(),
                sancion.getTipoContenido(), sancion.getIdContenido(), sancion.isActiva(), moderador.getSubject());
    }

    private Reporte validarReporte(SancionRequest datos) {
        Reporte reporte = reporteRepository.findById(datos.reporteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reporte con id " + datos.reporteId()));
        if (reporte.getTipoContenido() != datos.tipoContenido() || !reporte.getIdContenido().equals(datos.idContenido())) {
            throw new SolicitudInvalidaException("El reporte " + reporte.getId() + " corresponde al "
                    + reporte.getTipoContenido() + " " + reporte.getIdContenido() + ", no al "
                    + datos.tipoContenido() + " " + datos.idContenido());
        }
        if (reporte.getEstado() != EstadoReporte.PENDIENTE) {
            throw new ConflictoException("El reporte " + reporte.getId() + " ya esta " + reporte.getEstado()
                    + "; solo se pueden sancionar reportes pendientes");
        }
        return reporte;
    }

    private void validarSinDuplicado(TipoContenido tipoContenido, Long idContenido, TipoSancion tipo, Long excluirId) {
        if (sancionRepository.existeActiva(tipoContenido, idContenido, Set.of(tipo), excluirId)) {
            throw new ConflictoException("El " + tipoContenido + " " + idContenido + " ya tiene una sancion "
                    + tipo + " activa");
        }
    }

    private Sancion buscarSancion(Long id) {
        return sancionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sancion con id " + id));
    }
}
