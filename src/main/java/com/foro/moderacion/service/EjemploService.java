package com.foro.moderacion.service;

import com.foro.moderacion.dto.EjemploRequest;
import com.foro.moderacion.dto.EjemploResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface EjemploService {

    List<EjemploResponse> getAllEjemplos();

    EjemploResponse getEjemploById(Long id);

    EjemploResponse createEjemplo(EjemploRequest ejemplo, Jwt usuario);

    EjemploResponse updateEjemplo(Long id, EjemploRequest ejemplo, Jwt usuario);

    void deleteEjemplo(Long id, Jwt usuario);
}
