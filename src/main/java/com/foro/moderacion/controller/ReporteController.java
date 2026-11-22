package com.foro.moderacion.controller;

import com.foro.moderacion.dto.ReporteEstadoRequest;
import com.foro.moderacion.dto.ReporteRequest;
import com.foro.moderacion.dto.ReporteResponse;
import com.foro.moderacion.model.EstadoReporte;
import com.foro.moderacion.service.ReporteService;
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
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public List<ReporteResponse> getReportes(@RequestParam(required = false) EstadoReporte estado) {
        return reporteService.getReportes(estado);
    }

    @GetMapping("/{id}")
    public ReporteResponse getReporteById(@PathVariable Long id) {
        return reporteService.getReporteById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteResponse createReporte(@Valid @RequestBody ReporteRequest reporte, @AuthenticationPrincipal Jwt usuario) {
        return reporteService.createReporte(reporte, usuario);
    }

    @PutMapping("/{id}")
    public ReporteResponse updateEstado(@PathVariable Long id, @Valid @RequestBody ReporteEstadoRequest estado,
                                        @AuthenticationPrincipal Jwt usuario) {
        return reporteService.updateEstado(id, estado, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReporte(@PathVariable Long id, @AuthenticationPrincipal Jwt usuario) {
        reporteService.deleteReporte(id, usuario);
    }
}
