package com.foro.moderacion.repository;

import com.foro.moderacion.model.Ejemplo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjemploRepository extends JpaRepository<Ejemplo, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
