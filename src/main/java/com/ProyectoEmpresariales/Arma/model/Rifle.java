package com.ProyectoEmpresariales.Arma.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RIFLES")
@DiscriminatorValue("Rifle")
public class Rifle extends Arma {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MUNICION_ID", nullable = false)
    @JsonBackReference
    private Municion tipoMunicion;

    @Column(nullable = false)
    private double velocidad;

    // Para JPA
    protected Rifle() {
        super();
    }

    @JsonCreator
    public Rifle(@JsonProperty("dano") int daño,
                 @JsonProperty("municion") int municion,
                 @JsonProperty("nombre") String nombre,
                 @JsonProperty("vida") int vida,
                 @JsonProperty("velocidad") double velocidad,
                 @JsonProperty("fechaCreacion") LocalDateTime fecha,
                 @JsonProperty("tipoMunicion") Municion tipoMunicion) {

        super(daño, municion, nombre, vida, fecha);
        this.setFechaCreacion(fecha);
        this.velocidad = velocidad;
        this.tipoMunicion = tipoMunicion;
    }

    public Municion getTipoMunicion() {
        return tipoMunicion;
    }

    public void setTipoMunicion(Municion tipoMunicion) {
        this.tipoMunicion = tipoMunicion;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public double getVelocidad() {
        return velocidad;
    }

    public boolean engatillado() {
        double random = Math.random(); // Número aleatorio entre 0 y 1
        return random < .4;
    }

    @Override
    public synchronized void recargar() {
        int tiempoRecarga = 3000;
        int temp = (int) (Math.round(getDaño() * 0.2) * 10);
        if(temp > tiempoRecarga){
            tiempoRecarga = temp;
        }

        if (engatillado()) {
            System.out.println("¡El arma se ha engatillado! El tiempo de recarga aumentará.");
            tiempoRecarga *= 2;
        }

        try {
            System.out.println("Recargando...");
            Thread.sleep(tiempoRecarga);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("Fue interrumpida la recarga.");
        }

        setMunicion(getCapMunicion());
        System.out.println("Recarga completada. Munición: " + getMunicion());
    }
}