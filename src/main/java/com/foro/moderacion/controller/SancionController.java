package com.foro.moderacion.controller;

import com.foro.moderacion.dto.EstadoContenidoResponse;
import com.foro.moderacion.dto.SancionRequest;
import com.foro.moderacion.dto.SancionResponse;
import com.foro.moderacion.dto.SancionUpdateRequest;
import com.foro.moderacion.model.TipoContenido;
import com.foro.moderacion.model.TipoSancion;
import com.foro.moderacion.service.SancionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sanciones")
public class SancionController {

    @Autowired
    private SancionService sancionService;

    @GetMapping
    public List<SancionResponse> getSanciones(@RequestParam(required = false) TipoContenido tipoContenido,
                                              @RequestParam(required = false) TipoSancion tipoSancion,
                                              @RequestParam(required = false) Boolean activa) {
        return sancionService.getSanciones(tipoContenido, tipoSancion, activa);
    }

    @GetMapping("/activas")
    public EstadoContenidoResponse getEstadoContenido(@RequestParam TipoContenido tipo, @RequestParam Long id) {
        return sancionService.getEstadoContenido(tipo, id);
    }

    @GetMapping("/{id}")
    public SancionResponse getSancionById(@PathVariable Long id) {
        return sancionService.getSancionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SancionResponse createSancion(@Valid @RequestBody SancionRequest sancion, @AuthenticationPrincipal Jwt moderador) {
        return sancionService.createSancion(sancion, moderador);
    }

    @PutMapping("/{id}")
    public SancionResponse updateSancion(@PathVariable Long id, @Valid @RequestBody SancionUpdateRequest sancion,
                                         @AuthenticationPrincipal Jwt moderador) {
        return sancionService.updateSancion(id, sancion, moderador);
    }

    @PutMapping("/{id}/revocar")
    public SancionResponse revocarSancion(@PathVariable Long id, @AuthenticationPrincipal Jwt moderador) {
        return sancionService.revocarSancion(id, moderador);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSancion(@PathVariable Long id, @AuthenticationPrincipal Jwt moderador) {
        sancionService.deleteSancion(id, moderador);
    }
}
