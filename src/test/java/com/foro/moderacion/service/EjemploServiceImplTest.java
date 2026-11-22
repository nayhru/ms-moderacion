package com.foro.moderacion.service;

import com.foro.moderacion.dto.EjemploRequest;
import com.foro.moderacion.dto.EjemploResponse;
import com.foro.moderacion.exception.AccesoDenegadoException;
import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.model.Ejemplo;
import com.foro.moderacion.repository.EjemploRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjemploServiceImplTest {

    @Mock
    private EjemploRepository ejemploRepository;

    @InjectMocks
    private EjemploServiceImpl ejemploService;

    private static Jwt token(long id, String nombre, String rol) {
        return Jwt.withTokenValue("token").header("alg", "HS256")
                .subject(nombre).claim("uid", id).claim("rol", rol).build();
    }

    @Test
    void elAutorSeTomaDelToken() {
        when(ejemploRepository.save(any(Ejemplo.class))).thenAnswer(llamada -> llamada.getArgument(0));

        EjemploResponse creado = ejemploService.createEjemplo(new EjemploRequest("Prueba", null),
                token(3, "camila.rojas", "USUARIO"));

        assertEquals(3L, creado.usuarioId());
        assertEquals("camila.rojas", creado.autor());
    }

    @Test
    void noPermiteNombresRepetidos() {
        when(ejemploRepository.existsByNombreIgnoreCase("Prueba")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> ejemploService.createEjemplo(
                new EjemploRequest("Prueba", null), token(3, "camila.rojas", "USUARIO")));
        verify(ejemploRepository, never()).save(any());
    }

    @Test
    void otroUsuarioNoPuedeEliminar() {
        Ejemplo ejemplo = new Ejemplo();
        ejemplo.setId(1L);
        ejemplo.setUsuarioId(3L);
        when(ejemploRepository.findById(1L)).thenReturn(Optional.of(ejemplo));

        assertThrows(AccesoDenegadoException.class,
                () -> ejemploService.deleteEjemplo(1L, token(4, "diego.soto", "USUARIO")));
        verify(ejemploRepository, never()).delete(any());
    }
}
