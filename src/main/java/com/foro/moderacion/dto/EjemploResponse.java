package com.foro.moderacion.dto;

import com.foro.moderacion.model.Ejemplo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EjemploResponse(
        Long id,
        String nombre,
        String descripcion,
        Long usuarioId,
        String autor,
        LocalDateTime fechaCreacion) {

    public static EjemploResponse desde(Ejemplo ejemplo) {
        return EjemploResponse.builder()
                .id(ejemplo.getId())
                .nombre(ejemplo.getNombre())
                .descripcion(ejemplo.getDescripcion())
                .usuarioId(ejemplo.getUsuarioId())
                .autor(ejemplo.getAutor())
                .fechaCreacion(ejemplo.getFechaCreacion())
                .build();
    }
}
