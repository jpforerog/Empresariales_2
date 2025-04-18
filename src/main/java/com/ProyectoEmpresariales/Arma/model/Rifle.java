package com.ProyectoEmpresariales.Arma.model;


import com.ProyectoEmpresariales.Arma.servicios.ServicioMunicion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rifle extends Arma {

    private Municion tipoMunicion;
    private double velocidad;

    private ServicioMunicion servicioMunicion = ServicioMunicion.getInstancia();


    @JsonCreator
    public Rifle(@JsonProperty("dano") int daño,
                 @JsonProperty("municion") int municion,
                 @JsonProperty("nombre") String nombre,
                 @JsonProperty("vida") int vida,
                 @JsonProperty("velocidad") double velocidad,
                 @JsonProperty("fechaCreacion") LocalDateTime fecha,
                 @JsonProperty("tipoMunicion") Municion tipoMunicion){

        super(daño, municion, nombre, vida,fecha);
        this.setFechaCreacion(fecha);
        this.velocidad = velocidad;
        if (tipoMunicion.getNombre() == null){
            for (Municion mun : servicioMunicion.getMuniciones()){
                if(mun.getIndex()==0){
                    this.tipoMunicion = mun;
                }
            }
        }else{
            this.tipoMunicion = tipoMunicion;
        }
    }


    @Override
    public Rifle clone() {
        return (Rifle) super.clone();
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

    public double getVelocidad() {
        return velocidad;
    }




    public String toStringCompleto() {
        return "Rifle{" + "da\u00f1o=" + getDaño() + ", municion=" + getMunicion() + ", nombre=" + getNombre()
                + ", fechaCreacion=" + getFechaCreacion() + ", capMunicion=" + getMunicion()
                + ", vida=" + getVida() + ", velocidad = " + velocidad + ", tipo de municion= " + tipoMunicion.toString() + '}';
    }

    @Override
    public String toString() {
        return "Rifle -> " + getNombre() + " con daño de " + getDaño() + " y vida de "+getVida();
    }


    public boolean engatillado() {

        double random = Math.random(); // Número aleatorio entre 0 y 1

        // Si el número aleatorio es menor que la probabilidad, ocurre un engatillado

        return random < .4;
    }

    @Override
    public synchronized void recargar() {
        int tiempoRecarga = 3000;
        int temp = (int) (Math.round(getDaño() * 0.2) * 10); //El tiempo de recarga depende del daño, puesto que asi se penaliza las armas con demasiado daño
        if(temp>tiempoRecarga){
            tiempoRecarga=temp;
        }
        System.out.println(tiempoRecarga);
        // Verificar si ocurre un engatillado

        if (engatillado()) {
            System.out.println("¡El arma se ha engatillado! El tiempo de recarga aumentará.");
            tiempoRecarga *= 2; // Duplicar el tiempo de recarga (puedes ajustar este factor)
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
        System.out.println("_______________________");
    }




}