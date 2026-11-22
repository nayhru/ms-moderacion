package com.foro.moderacion.service.estrategia;

import com.foro.moderacion.exception.AccesoDenegadoException;
import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoSancion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

// ELIMINAR: es la sancion mas grave, por eso solo la puede aplicar un ADMIN
@Slf4j
@Component
public class EliminarSancionStrategy implements SancionStrategy {

    @Override
    public TipoSancion tipo() {
        return TipoSancion.ELIMINAR;
    }

    @Override
    public boolean bloqueaContenido() {
        return true;
    }

    @Override
    public void aplicar(Sancion sancion, Jwt moderador) {
        if (!"ADMIN".equals(moderador.getClaimAsString("rol"))) {
            throw new AccesoDenegadoException("Solo un ADMIN puede aplicar la sancion ELIMINAR");
        }
        log.warn("Estrategia ELIMINAR: el {} {} queda marcado para eliminacion definitiva, admin={}",
                sancion.getTipoContenido(), sancion.getIdContenido(), moderador.getSubject());
    }
}
