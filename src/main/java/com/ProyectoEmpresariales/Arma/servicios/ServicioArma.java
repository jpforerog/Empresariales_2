package com.ProyectoEmpresariales.Arma.servicios;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.repository.ArmaRepository;
import com.ProyectoEmpresariales.Arma.repository.RifleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ServicioArma {
    @Autowired
    private ArmaRepository armaRepository;

    @Autowired
    private RifleRepository rifleRepository;



    @Transactional
    public Arma añadirArma(Arma arma) throws Exception {
        // Verificar que no exista otra arma con el mismo nombre y tipo
        if (armaRepository.existsByNombre(arma.getNombre())) {
            throw new Exception("Arma con el mismo nombre y mismo tipo");
        }

        // Guardar en la base de datos
        return armaRepository.save(arma);
    }

    @Transactional(readOnly = true)
    public List<Arma> getArmas() {
        return armaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rifle> getRifles() {
        return rifleRepository.findAll();
    }

    @Transactional
    public void eliminarArma(Arma arma) {
        armaRepository.delete(arma);
    }

    @Transactional
    public Arma actualizarArma(Arma oldArma, Arma newArma) throws Exception {
        // Verificar que el nuevo nombre no esté en uso por otra arma
        if (!oldArma.getNombre().equals(newArma.getNombre()) &&
                armaRepository.existsByNombre(newArma.getNombre())) {
            throw new Exception("Otra arma con el mismo nombre ya fue creada");
        }

        // Mantener el ID del arma original
        newArma.setId(oldArma.getId());

        return armaRepository.save(newArma);
    }

    @Transactional(readOnly = true)
    public Optional<Arma> findById(Long id) {
        return armaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Arma> findByNombre(String nombre) {
        return armaRepository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public List<Arma> findByVidaMinima(int vidaMinima) {
        return armaRepository.findByVidaGreaterThanEqual(vidaMinima);
    }

    @Transactional(readOnly = true)
    public List<Arma> findByDañoMinimo(int dañoMinimo) {
        return armaRepository.findByDañoGreaterThanEqual(dañoMinimo);
    }

    @Transactional(readOnly = true)
    public List<Arma> findByTipo(String tipo) {
        if (tipo == null) {
            return List.of();
        }

        String tipoNormalizado = tipo.toLowerCase();
        switch (tipoNormalizado) {
            case "rifle":
                return armaRepository.findByTipoArma("Rifle");
            default:
                return List.of();
        }
    }
}