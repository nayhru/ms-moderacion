package com.foro.moderacion.controller;

import com.foro.moderacion.dto.EjemploRequest;
import com.foro.moderacion.dto.EjemploResponse;
import com.foro.moderacion.service.EjemploService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ejemplos")
public class EjemploController {

    @Autowired
    private EjemploService ejemploService;

    @GetMapping
    public List<EjemploResponse> getAllEjemplos() {
        return ejemploService.getAllEjemplos();
    }

    @GetMapping("/{id}")
    public EjemploResponse getEjemploById(@PathVariable Long id) {
        return ejemploService.getEjemploById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EjemploResponse createEjemplo(@Valid @RequestBody EjemploRequest ejemplo, @AuthenticationPrincipal Jwt usuario) {
        return ejemploService.createEjemplo(ejemplo, usuario);
    }

    @PutMapping("/{id}")
    public EjemploResponse updateEjemplo(@PathVariable Long id, @Valid @RequestBody EjemploRequest ejemplo,
                                         @AuthenticationPrincipal Jwt usuario) {
        return ejemploService.updateEjemplo(id, ejemplo, usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEjemplo(@PathVariable Long id, @AuthenticationPrincipal Jwt usuario) {
        ejemploService.deleteEjemplo(id, usuario);
    }
}
