package com.foro.moderacion.dto;

import com.foro.moderacion.model.TipoSancion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SancionUpdateRequest(
        @NotNull(message = "El tipo de sancion es obligatorio (OCULTAR, ADVERTENCIA o ELIMINAR)")
        TipoSancion tipoSancion,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String motivo) {
}
