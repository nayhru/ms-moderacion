package com.foro.moderacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORTE")
@Getter
@Setter
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REPORTE")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONTENIDO", nullable = false, updatable = false, length = 20)
    private TipoContenido tipoContenido;

    // Id del tema o comentario en ms-foro: aqui solo se guarda la referencia
    @Column(name = "ID_CONTENIDO", nullable = false, updatable = false)
    private Long idContenido;

    @Column(name = "MOTIVO", nullable = false, length = 500)
    private String motivo;

    @Column(name = "ID_USUARIO", nullable = false, updatable = false)
    private Long usuarioId;

    @Column(name = "AUTOR_REPORTE", nullable = false, updatable = false, length = 30)
    private String autorReporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoReporte estado;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoReporte.PENDIENTE;
        }
    }
}
