package com.foro.moderacion.dto;

import com.foro.moderacion.model.EstadoReporte;
import jakarta.validation.constraints.NotNull;

public record ReporteEstadoRequest(
        @NotNull(message = "El estado es obligatorio (PENDIENTE, RESUELTO o DESCARTADO)")
        EstadoReporte estado) {
}
