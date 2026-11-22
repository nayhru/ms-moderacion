package com.foro.moderacion.dto;

import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SancionRequest(
        @NotNull(message = "El tipo de contenido es obligatorio (TEMA o COMENTARIO)")
        TipoContenido tipoContenido,

        @NotNull(message = "El id del contenido es obligatorio")
        @Positive(message = "El id del contenido debe ser mayor que cero")
        Long idContenido,

        @NotNull(message = "El tipo de sancion es obligatorio (OCULTAR, ADVERTENCIA o ELIMINAR)")
        TipoSancion tipoSancion,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String motivo,

        // Opcional: si viene, el reporte queda RESUELTO al crear la sancion
        @Positive(message = "El id del reporte debe ser mayor que cero")
        Long reporteId) {
}
