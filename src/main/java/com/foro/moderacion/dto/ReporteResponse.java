package com.foro.moderacion.dto;

import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.model.Reporte;
import com.foro.moderacion.model.TipoContenido;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReporteResponse(
        Long id,
        TipoContenido tipoContenido,
        Long idContenido,
        String motivo,
        Long usuarioId,
        String autorReporte,
        EstadoReporte estado,
        LocalDateTime fechaCreacion) {

    public static ReporteResponse desde(Reporte reporte) {
        return ReporteResponse.builder()
                .id(reporte.getId())
                .tipoContenido(reporte.getTipoContenido())
                .idContenido(reporte.getIdContenido())
                .motivo(reporte.getMotivo())
                .usuarioId(reporte.getUsuarioId())
                .autorReporte(reporte.getAutorReporte())
                .estado(reporte.getEstado())
                .fechaCreacion(reporte.getFechaCreacion())
                .build();
    }
}
