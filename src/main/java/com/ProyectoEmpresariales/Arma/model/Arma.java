package com.ProyectoEmpresariales.Arma.model;


import java.lang.reflect.Field;
import java.time.LocalDateTime;


public class Arma implements Cloneable {

    private int daño;
    private int municion;
    private String nombre;
    private LocalDateTime fechaCreacion;


    public Arma() {
    }

    //Nuevos atributos
    private int capMunicion;
    private int vida = 100;
    private final int distancia = 100;
    private int index;


    public Arma(int daño, int municion, String nombre, int vida,LocalDateTime fechaCreacion) {

        this.daño = daño;
        this.municion = municion;
        this.capMunicion = municion;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.vida = vida;
    }


    @Override
    public Arma clone() {
        try {
            return (Arma) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error al clonar el objeto Arma", e);
        }
    }
    public String toStringCompleto() {return null;}
    public double getVelocidad(){return 0;}

    public int getVida() {
        return vida;
    }

    public int getDistancia() {
        return distancia;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getDaño() {
        return daño;
    }

    public void setDaño(int daño) {
        this.daño = daño;
    }

    public int getMunicion() {
        return municion;
    }

    public void setMunicion(int municion) {
        this.municion = municion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getCapMunicion() {
        return capMunicion;
    }

    public void setCapMunicion(int capMunicion) {
        this.capMunicion = capMunicion;
    }


    public static String[] obtenerNombresAtributos(Object objeto) {
        // Obtener la clase del objeto
        Class<?> clase = objeto.getClass();

        // Obtener todos los campos (atributos) declarados en la clase
        Field[] campos = clase.getDeclaredFields();

        // Crear un arreglo para almacenar los nombres de los atributos
        String[] nombres = new String[campos.length];

        // Recorrer los campos y obtener sus nombres
        for (int i = 0; i < campos.length; i++) {
            nombres[i] = campos[i].getName();
        }

        return nombres;
    }

    public boolean enemigoVivo(Arma enemigo) {
        if (enemigo.getVida() <= 0) {
            return false;
        }
        return true;
    }


    public Arma disparar(Arma objetivoConVida) {
        if (municion == 0) {
            System.out.println("Estas recargando calmate");

        } else {
            if (enemigoVivo(objetivoConVida)) {
                municion -= 1;
                objetivoConVida.setVida(objetivoConVida.getVida() - this.getDaño());

            }
        }
        return objetivoConVida;
    }


    public void recargar() {
        System.out.println("recarga de arma");
        int tiempoRecarga = (int) (Math.round(daño * 0.2) * 10); //El tiempo de recarga depende del daño, puesto que asi se penaliza las armas con demasiado daño
        try {
            Thread.sleep(tiempoRecarga);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            System.out.println("Fue interrumpida la recarga.");
        }
        municion = capMunicion;
        System.out.println("Recarga completada. Munición: " + municion);

    }



}
