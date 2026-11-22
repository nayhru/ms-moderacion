package com.foro.moderacion.service;

import com.foro.moderacion.dto.EstadoContenidoResponse;
import com.foro.moderacion.dto.SancionRequest;
import com.foro.moderacion.dto.SancionResponse;
import com.foro.moderacion.dto.SancionUpdateRequest;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface SancionService {

    List<SancionResponse> getSanciones(TipoContenido tipoContenido, TipoSancion tipoSancion, Boolean activa);

    SancionResponse getSancionById(Long id);

    EstadoContenidoResponse getEstadoContenido(TipoContenido tipoContenido, Long idContenido);

    SancionResponse createSancion(SancionRequest sancion, Jwt moderador);

    SancionResponse updateSancion(Long id, SancionUpdateRequest sancion, Jwt moderador);

    SancionResponse revocarSancion(Long id, Jwt moderador);

    void deleteSancion(Long id, Jwt moderador);
}
