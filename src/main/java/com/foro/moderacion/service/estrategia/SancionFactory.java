package com.foro.moderacion.service.estrategia;

import com.foro.moderacion.model.TipoSancion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Patron Factory: entrega la estrategia que corresponde a cada TIPO_SANCION.
// Spring inyecta todas las implementaciones de SancionStrategy, asi que un tipo nuevo solo necesita su clase.
@Slf4j
@Component
public class SancionFactory {

    private final Map<TipoSancion, SancionStrategy> estrategias = new EnumMap<>(TipoSancion.class);

    public SancionFactory(List<SancionStrategy> disponibles) {
        disponibles.forEach(estrategia -> estrategias.put(estrategia.tipo(), estrategia));
        log.info("SancionFactory lista con las estrategias {}", estrategias.keySet());
    }

    public SancionStrategy obtener(TipoSancion tipo) {
        SancionStrategy estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalStateException("No hay una estrategia registrada para la sancion " + tipo);
        }
        log.debug("SancionFactory entrega {} para el tipo {}", estrategia.getClass().getSimpleName(), tipo);
        return estrategia;
    }
}
