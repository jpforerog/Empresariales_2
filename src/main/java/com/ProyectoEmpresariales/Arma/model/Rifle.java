package com.ProyectoEmpresariales.Arma.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rifle extends Arma {

    private int cadenciaDisparo;
    private double velocidad;



    @JsonCreator
    public Rifle(@JsonProperty("dano") int daño,
                 @JsonProperty("municion") int municion,
                 @JsonProperty("nombre") String nombre,
                 @JsonProperty("vida") int vida,
                 @JsonProperty("velocidad") double velocidad,
                 @JsonProperty("fechaCreacion") LocalDateTime fecha){

        super(daño, municion, nombre, vida,fecha);
        this.setFechaCreacion(fecha);
        this.velocidad = velocidad;

    }

    @Override
    public Rifle clone() {
        return (Rifle) super.clone();
    }

    public int getCadenciaDisparo() {
        return cadenciaDisparo;
    }

    public void setCadenciaDisparo(int cadenciaDisparo) {
        this.cadenciaDisparo = cadenciaDisparo;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }


    public String toStringCompleto() {
        return "Rifle{" + "da\u00f1o=" + getDaño() + ", municion=" + getMunicion() + ", nombre=" + getNombre()
                + ", fechaCreacion=" + getFechaCreacion() + ", capMunicion=" + getMunicion()
                + ", vida=" + getVida() + ", velocidad = " + velocidad + ", cadencia de disparo= " + cadenciaDisparo + '}';
    }

    @Override
    public String toString() {
        return "Rifle -> " + getNombre() + " con daño de " + getDaño() + " y vida de "+getVida();
    }


    public boolean engatillado() {

        double probEngatillado = cadenciaDisparo > 500 ? .4 : .2;
        double random = Math.random(); // Número aleatorio entre 0 y 1

        // Si el número aleatorio es menor que la probabilidad, ocurre un engatillado

        return random < probEngatillado;
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