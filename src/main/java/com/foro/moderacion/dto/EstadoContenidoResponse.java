package com.foro.moderacion.dto;

import com.foro.moderacion.model.TipoContenido;
import lombok.Builder;

import java.util.List;

// Respuesta publica para que el foro sepa si debe mostrar un tema o comentario
@Builder
public record EstadoContenidoResponse(
        TipoContenido tipoContenido,
        Long idContenido,
        boolean baneado,
        List<SancionResponse> sancionesActivas) {
}
