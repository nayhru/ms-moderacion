package com.foro.moderacion.service.estrategia;

import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoSancion;
import com.foro.moderacion.repository.SancionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;

// OCULTAR: el contenido deja de mostrarse, pero no tiene sentido si ya esta marcado para eliminacion
@Slf4j
@Component
public class OcultarSancionStrategy implements SancionStrategy {

    @Autowired
    private SancionRepository sancionRepository;

    @Override
    public TipoSancion tipo() {
        return TipoSancion.OCULTAR;
    }

    @Override
    public boolean bloqueaContenido() {
        return true;
    }

    @Override
    public void aplicar(Sancion sancion, Jwt moderador) {
        if (sancionRepository.existeActiva(sancion.getTipoContenido(), sancion.getIdContenido(),
                Set.of(TipoSancion.ELIMINAR), sancion.getId())) {
            throw new ConflictoException("El " + sancion.getTipoContenido() + " " + sancion.getIdContenido()
                    + " ya esta marcado para eliminacion; ocultarlo no tiene efecto");
        }
        log.info("Estrategia OCULTAR: el {} {} deja de mostrarse en el foro, moderador={}",
                sancion.getTipoContenido(), sancion.getIdContenido(), moderador.getSubject());
    }
}
