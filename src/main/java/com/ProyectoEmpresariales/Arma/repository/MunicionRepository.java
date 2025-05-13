package com.ProyectoEmpresariales.Arma.repository;

import com.ProyectoEmpresariales.Arma.model.Municion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicionRepository extends JpaRepository<Municion, Long> {

    Optional<Municion> findByNombre(String nombre);

    List<Municion> findByCadenciaGreaterThanEqual(int cadenciaMinima);

    List<Municion> findByDañoArea(boolean dañoArea);

    boolean existsByNombre(String nombre);

}