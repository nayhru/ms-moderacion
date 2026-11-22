package com.foro.moderacion.service;

import com.foro.moderacion.exception.AccesoDenegadoException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

// Reglas compartidas: el autor, un MODERADOR o un ADMIN pueden editar o eliminar contenido
final class Permisos {

    private static final Set<String> ROLES_DE_GESTION = Set.of("ADMIN", "MODERADOR");

    private Permisos() {
    }

    static Long idUsuario(Jwt usuario) {
        return ((Number) usuario.getClaim("uid")).longValue();
    }

    static boolean esGestor(Jwt usuario) {
        return ROLES_DE_GESTION.contains(usuario.getClaimAsString("rol"));
    }

    static void validarAutorOGestor(Long idAutor, Jwt usuario) {
        if (!idAutor.equals(idUsuario(usuario)) && !esGestor(usuario)) {
            throw new AccesoDenegadoException("Solo el autor, un MODERADOR o un ADMIN pueden realizar esta accion");
        }
    }
}
