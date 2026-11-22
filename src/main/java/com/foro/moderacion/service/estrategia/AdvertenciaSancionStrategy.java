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

// ADVERTENCIA: el contenido sigue visible y solo se deja constancia; no aplica sobre contenido ya oculto o eliminado
@Slf4j
@Component
public class AdvertenciaSancionStrategy implements SancionStrategy {

    @Autowired
    private SancionRepository sancionRepository;

    @Override
    public TipoSancion tipo() {
        return TipoSancion.ADVERTENCIA;
    }

    @Override
    public boolean bloqueaContenido() {
        return false;
    }

    @Override
    public void aplicar(Sancion sancion, Jwt moderador) {
        if (sancionRepository.existeActiva(sancion.getTipoContenido(), sancion.getIdContenido(),
                Set.of(TipoSancion.OCULTAR, TipoSancion.ELIMINAR), sancion.getId())) {
            throw new ConflictoException("El " + sancion.getTipoContenido() + " " + sancion.getIdContenido()
                    + " ya esta oculto o eliminado; una advertencia no tiene efecto");
        }
        log.info("Estrategia ADVERTENCIA: el {} {} sigue visible y su autor queda advertido, moderador={}",
                sancion.getTipoContenido(), sancion.getIdContenido(), moderador.getSubject());
    }
}
