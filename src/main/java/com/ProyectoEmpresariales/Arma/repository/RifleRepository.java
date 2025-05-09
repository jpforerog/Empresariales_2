package com.ProyectoEmpresariales.Arma.repository;

import com.ProyectoEmpresariales.Arma.model.Municion;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RifleRepository extends JpaRepository<Rifle, Long> {

    List<Rifle> findByTipoMunicion(Municion tipoMunicion);

    List<Rifle> findByVelocidadGreaterThanEqual(double velocidadMinima);
}