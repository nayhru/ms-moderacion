package com.foro.moderacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Entidad de ejemplo: reemplazala por las de tu microservicio
@Entity
@Table(name = "EJEMPLO")
@Getter
@Setter
@NoArgsConstructor
public class Ejemplo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EJEMPLO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    // El usuario pertenece a ms-usuarios: aqui solo se guarda su referencia
    @Column(name = "ID_USUARIO", nullable = false, updatable = false)
    private Long usuarioId;

    @Column(name = "AUTOR", nullable = false, updatable = false, length = 30)
    private String autor;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
    }
}
