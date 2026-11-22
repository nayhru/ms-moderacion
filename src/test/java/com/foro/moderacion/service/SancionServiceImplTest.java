package com.foro.moderacion.service;

import com.foro.moderacion.dto.SancionRequest;
import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.exception.SolicitudInvalidaException;
import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.model.Reporte;
import com.foro.moderacion.model.Sancion;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import com.foro.moderacion.repository.ReporteRepository;
import com.foro.moderacion.repository.SancionRepository;
import com.foro.moderacion.service.estrategia.SancionFactory;
import com.foro.moderacion.service.estrategia.SancionStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SancionServiceImplTest {

    @Mock
    private SancionRepository sancionRepository;

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private SancionFactory sancionFactory;

    @Mock
    private SancionStrategy estrategia;

    @InjectMocks
    private SancionServiceImpl sancionService;

    private final Jwt moderador = Jwt.withTokenValue("token").header("alg", "HS256")
            .subject("moderador").claim("uid", 2L).claim("rol", "MODERADOR").build();

    private static Reporte reporte(TipoContenido tipo, long idContenido, EstadoReporte estado) {
        Reporte reporte = new Reporte();
        reporte.setId(7L);
        reporte.setTipoContenido(tipo);
        reporte.setIdContenido(idContenido);
        reporte.setEstado(estado);
        return reporte;
    }

    @Test
    void alSancionarUnReporteQuedaResuelto() {
        Reporte pendiente = reporte(TipoContenido.TEMA, 4, EstadoReporte.PENDIENTE);
        when(reporteRepository.findById(7L)).thenReturn(Optional.of(pendiente));
        when(sancionFactory.obtener(TipoSancion.OCULTAR)).thenReturn(estrategia);
        when(sancionRepository.save(any(Sancion.class))).thenAnswer(llamada -> llamada.getArgument(0));

        sancionService.createSancion(new SancionRequest(TipoContenido.TEMA, 4L, TipoSancion.OCULTAR,
                "Tema repetido varias veces", 7L), moderador);

        assertEquals(EstadoReporte.RESUELTO, pendiente.getEstado());
        verify(estrategia).aplicar(any(Sancion.class), eq(moderador));
    }

    @Test
    void noSeDuplicaUnaSancionActivaDelMismoTipo() {
        when(sancionRepository.existeActiva(eq(TipoContenido.COMENTARIO), anyLong(), anySet(), isNull())).thenReturn(true);

        assertThrows(ConflictoException.class, () -> sancionService.createSancion(new SancionRequest(
                TipoContenido.COMENTARIO, 1L, TipoSancion.OCULTAR, "Publicidad no permitida", null), moderador));
        verify(sancionRepository, never()).save(any());
    }

    @Test
    void elReporteDebeCorresponderAlContenidoSancionado() {
        when(reporteRepository.findById(7L)).thenReturn(Optional.of(reporte(TipoContenido.COMENTARIO, 3, EstadoReporte.PENDIENTE)));

        assertThrows(SolicitudInvalidaException.class, () -> sancionService.createSancion(new SancionRequest(
                TipoContenido.TEMA, 3L, TipoSancion.OCULTAR, "Contenido fuera de las normas", 7L), moderador));
    }
}
