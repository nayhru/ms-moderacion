package com.foro.moderacion.service;

import com.foro.moderacion.dto.EjemploRequest;
import com.foro.moderacion.dto.EjemploResponse;
import com.foro.moderacion.exception.ConflictoException;
import com.foro.moderacion.exception.RecursoNoEncontradoException;
import com.foro.moderacion.model.Ejemplo;
import com.foro.moderacion.repository.EjemploRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EjemploServiceImpl implements EjemploService {

    @Autowired
    private EjemploRepository ejemploRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EjemploResponse> getAllEjemplos() {
        return ejemploRepository.findAll().stream().map(EjemploResponse::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EjemploResponse getEjemploById(Long id) {
        return EjemploResponse.desde(buscarEjemplo(id));
    }

    @Override
    @Transactional
    public EjemploResponse createEjemplo(EjemploRequest datos, Jwt usuario) {
        if (ejemploRepository.existsByNombreIgnoreCase(datos.nombre())) {
            throw new ConflictoException("Ya existe un ejemplo con el nombre " + datos.nombre());
        }
        Ejemplo ejemplo = new Ejemplo();
        ejemplo.setNombre(datos.nombre());
        ejemplo.setDescripcion(datos.descripcion());
        ejemplo.setUsuarioId(Permisos.idUsuario(usuario));
        ejemplo.setAutor(usuario.getSubject());
        Ejemplo guardado = ejemploRepository.save(ejemplo);
        log.info("Ejemplo creado: id={}, nombre={}, autor={}", guardado.getId(), guardado.getNombre(), guardado.getAutor());
        return EjemploResponse.desde(guardado);
    }

    @Override
    @Transactional
    public EjemploResponse updateEjemplo(Long id, EjemploRequest datos, Jwt usuario) {
        Ejemplo ejemplo = buscarEjemplo(id);
        Permisos.validarAutorOGestor(ejemplo.getUsuarioId(), usuario);
        if (ejemploRepository.existsByNombreIgnoreCaseAndIdNot(datos.nombre(), id)) {
            throw new ConflictoException("Ya existe un ejemplo con el nombre " + datos.nombre());
        }
        ejemplo.setNombre(datos.nombre());
        ejemplo.setDescripcion(datos.descripcion());
        log.info("Ejemplo actualizado: id={}, nombre={}, por={}", id, datos.nombre(), usuario.getSubject());
        return EjemploResponse.desde(ejemplo);
    }

    @Override
    @Transactional
    public void deleteEjemplo(Long id, Jwt usuario) {
        Ejemplo ejemplo = buscarEjemplo(id);
        Permisos.validarAutorOGestor(ejemplo.getUsuarioId(), usuario);
        ejemploRepository.delete(ejemplo);
        log.info("Ejemplo eliminado: id={}, nombre={}, por={}", id, ejemplo.getNombre(), usuario.getSubject());
    }

    private Ejemplo buscarEjemplo(Long id) {
        return ejemploRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el ejemplo con id " + id));
    }
}
