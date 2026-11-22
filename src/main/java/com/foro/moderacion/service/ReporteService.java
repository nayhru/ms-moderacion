package com.foro.moderacion.service;

import com.foro.moderacion.dto.ReporteEstadoRequest;
import com.foro.moderacion.dto.ReporteRequest;
import com.foro.moderacion.dto.ReporteResponse;
import com.foro.moderacion.model.EstadoReporte;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface ReporteService {

    List<ReporteResponse> getReportes(EstadoReporte estado);

    ReporteResponse getReporteById(Long id);

    ReporteResponse createReporte(ReporteRequest reporte, Jwt usuario);

    ReporteResponse updateEstado(Long id, ReporteEstadoRequest estado, Jwt usuario);

    void deleteReporte(Long id, Jwt usuario);
}
