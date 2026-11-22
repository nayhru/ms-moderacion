package com.foro.moderacion.service.estrategia;

import com.foro.moderacion.exception.AccesoDenegadoException;
import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SancionFactoryTest {

    private final SancionFactory factory = new SancionFactory(List.of(
            new OcultarSancionStrategy(), new AdvertenciaSancionStrategy(), new EliminarSancionStrategy()));

    private static Jwt token(String nombre, String rol) {
        return Jwt.withTokenValue("token").header("alg", "HS256")
                .subject(nombre).claim("uid", 1L).claim("rol", rol).build();
    }

    private static Sancion sancionSobreTema() {
        Sancion sancion = new Sancion();
        sancion.setTipoContenido(TipoContenido.TEMA);
        sancion.setIdContenido(4L);
        return sancion;
    }

    @Test
    void entregaLaEstrategiaDeCadaTipo() {
        assertInstanceOf(OcultarSancionStrategy.class, factory.obtener(TipoSancion.OCULTAR));
        assertInstanceOf(AdvertenciaSancionStrategy.class, factory.obtener(TipoSancion.ADVERTENCIA));
        assertInstanceOf(EliminarSancionStrategy.class, factory.obtener(TipoSancion.ELIMINAR));
    }

    @Test
    void soloLaAdvertenciaDejaVisibleElContenido() {
        assertTrue(factory.obtener(TipoSancion.OCULTAR).bloqueaContenido());
        assertTrue(factory.obtener(TipoSancion.ELIMINAR).bloqueaContenido());
        assertFalse(factory.obtener(TipoSancion.ADVERTENCIA).bloqueaContenido());
    }

    @Test
    void unModeradorNoPuedeEliminarContenido() {
        SancionStrategy eliminar = factory.obtener(TipoSancion.ELIMINAR);

        assertThrows(AccesoDenegadoException.class, () -> eliminar.aplicar(sancionSobreTema(), token("moderador", "MODERADOR")));
        assertDoesNotThrow(() -> eliminar.aplicar(sancionSobreTema(), token("admin", "ADMIN")));
    }
}
