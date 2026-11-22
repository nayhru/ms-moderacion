package com.foro.moderacion.dto;

import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SancionResponse(
        Long id,
        TipoContenido tipoContenido,
        Long idContenido,
        TipoSancion tipoSancion,
        String motivo,
        String moderador,
        boolean activa,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaRevocacion,
        Long reporteId) {

    public static SancionResponse desde(Sancion sancion) {
        return SancionResponse.builder()
                .id(sancion.getId())
                .tipoContenido(sancion.getTipoContenido())
                .idContenido(sancion.getIdContenido())
                .tipoSancion(sancion.getTipoSancion())
                .motivo(sancion.getMotivo())
                .moderador(sancion.getModerador())
                .activa(sancion.isActiva())
                .fechaCreacion(sancion.getFechaCreacion())
                .fechaRevocacion(sancion.getFechaRevocacion())
                .reporteId(sancion.getReporte() == null ? null : sancion.getReporte().getId())
                .build();
    }
}
