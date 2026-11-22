package com.foro.moderacion.service.estrategia;

import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoSancion;
import org.springframework.security.oauth2.jwt.Jwt;

// Patron Strategy: cada tipo de sancion tiene su propia regla y su propio efecto sobre el contenido.
// SancionFactory elige la implementacion segun el TIPO_SANCION.
public interface SancionStrategy {

    TipoSancion tipo();

    // true si el contenido deja de mostrarse en el foro mientras la sancion siga activa
    boolean bloqueaContenido();

    // Valida la regla del tipo y deja registro en el log; lanza una excepcion si la sancion no se puede aplicar
    void aplicar(Sancion sancion, Jwt moderador);
}
