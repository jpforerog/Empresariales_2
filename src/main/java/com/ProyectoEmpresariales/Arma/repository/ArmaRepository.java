package com.ProyectoEmpresariales.Arma.repository;

import com.ProyectoEmpresariales.Arma.model.Arma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArmaRepository extends JpaRepository<Arma, Long> {

    Optional<Arma> findByNombre(String nombre);

    List<Arma> findByVidaGreaterThanEqual(int vidaMinima);

    List<Arma> findByDañoGreaterThanEqual(int dañoMinimo);

    List<Arma> findByTipoArma(String tipoArma);

    boolean existsByNombre(String nombre);
}