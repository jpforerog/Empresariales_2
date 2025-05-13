package com.ProyectoEmpresariales.Arma.servicios;

import com.ProyectoEmpresariales.Arma.model.Municion;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.repository.MunicionRepository;
import com.ProyectoEmpresariales.Arma.repository.RifleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServicioMunicion {

    @Autowired
    private MunicionRepository municionRepository;

    @Autowired
    private RifleRepository rifleRepository;

    @PostConstruct
    public void init() {
        // Crear munición predeterminada si no existe
        if (municionRepository.count() == 0) {
            Municion predeterminada = Municion.builder()
                    .nombre("Predeterminado")
                    .dañoArea(false)
                    .cadencia(10)
                    .build();
            municionRepository.save(predeterminada);
        }
    }

    @Transactional
    public Municion añadirMunicion(Municion municion) throws Exception {
        // Validar que no exista otra munición con el mismo nombre
        if (municionRepository.existsByNombre(municion.getNombre())) {
            throw new Exception("Municion con el mismo nombre");
        }

        // Guardar en la base de datos
        return municionRepository.save(municion);
    }

    @Transactional(readOnly = true)
    public List<Municion> getMuniciones() {
        return municionRepository.findAll();
    }

    @Transactional
    public void eliminarMunicion(Municion municion) {
        // No permitir eliminar la munición predeterminada
        Municion predeterminada = getPredeterminada();
        if (municion.getId().equals(predeterminada.getId())) {
            return;
        }

        // Actualizar rifles que usan esta munición a la predeterminada
        List<Rifle> riflesAfectados = rifleRepository.findByTipoMunicion(municion);

        for (Rifle rifle : riflesAfectados) {
            rifle.setTipoMunicion(predeterminada);
            rifleRepository.save(rifle);
        }

        // Eliminar la munición
        municionRepository.delete(municion);
    }

    @Transactional
    public Municion actualizarMunicion(Municion oldMunicion, Municion newMunicion) throws Exception {
        // Verificar que el nuevo nombre no esté en uso por otra munición
        if (!oldMunicion.getNombre().equals(newMunicion.getNombre()) &&
                municionRepository.existsByNombre(newMunicion.getNombre())) {
            throw new Exception("Otra municion con el mismo nombre ya fue creada");
        }

        // Mantener el ID de la munición original
        newMunicion.setId(oldMunicion.getId());

        return municionRepository.save(newMunicion);
    }

    @Transactional(readOnly = true)
    public Municion getPredeterminada() {
        return municionRepository.findByNombre("Predeterminado")
                .orElseThrow(() -> new RuntimeException("Munición predeterminada no encontrada"));
    }

    @Transactional(readOnly = true)
    public Optional<Municion> findByNombre(String nombre) {
        return municionRepository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public Optional<Municion> findById(Long id) {
        return municionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Municion> findByCadenciaMinima(int cadenciaMinima) {
        return municionRepository.findByCadenciaGreaterThanEqual(cadenciaMinima);
    }

    @Transactional(readOnly = true)
    public List<Municion> findByDañoArea(boolean dañoArea) {
        return municionRepository.findByDañoArea(dañoArea);
    }
}