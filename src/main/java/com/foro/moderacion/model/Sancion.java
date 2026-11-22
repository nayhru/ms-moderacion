package com.foro.moderacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "SANCION")
@Getter
@Setter
@NoArgsConstructor
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SANCION")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONTENIDO", nullable = false, updatable = false, length = 20)
    private TipoContenido tipoContenido;

    @Column(name = "ID_CONTENIDO", nullable = false, updatable = false)
    private Long idContenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SANCION", nullable = false, length = 20)
    private TipoSancion tipoSancion;

    @Column(name = "MOTIVO", nullable = false, length = 500)
    private String motivo;

    // Nombre de usuario de quien aplico la sancion, tomado del token
    @Column(name = "MODERADOR", nullable = false, updatable = false, length = 30)
    private String moderador;

    // En la tabla es NUMBER(1): 1 activa, 0 revocada
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ACTIVA", nullable = false)
    private boolean activa;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_REVOCACION")
    private LocalDateTime fechaRevocacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REPORTE", updatable = false)
    private Reporte reporte;

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
        activa = true;
    }
}
