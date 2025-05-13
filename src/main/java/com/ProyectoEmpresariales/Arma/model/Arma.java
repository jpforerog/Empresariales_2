package com.ProyectoEmpresariales.Arma.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

@Entity
@Table(name = "ARMAS")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TIPO_ARMA", discriminatorType = DiscriminatorType.STRING)
public abstract class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "arma_seq")
    @SequenceGenerator(name = "arma_seq", sequenceName = "ARMA_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private int daño;

    @Column(nullable = false)
    private int municion;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    @Column(name = "CAP_MUNICION")
    private int capMunicion;

    @Column(nullable = false)
    private int vida = 100;

    @Column(nullable = false)
    private final int distancia = 100;

    @Column(name = "TIPO_ARMA", insertable = false, updatable = false)
    private String tipoArma;

    protected Arma() {
    }

    protected Arma(int daño, int municion, String nombre, int vida, LocalDateTime fechaCreacion) {
        this.daño = daño;
        this.municion = municion;
        this.capMunicion = municion;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.vida = vida;
    }

    public abstract double getVelocidad();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVida() {
        return vida;
    }

    public int getDistancia() {
        return distancia;
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

    public String getTipoArma() {
        // Si el tipoArma está establecido, devolverlo
        if (tipoArma != null) {
            return tipoArma;
        }

        // De lo contrario, determinar el tipo basado en la clase real
        String className = this.getClass().getSimpleName();
        if ("Rifle".equals(className)) {
            return "Rifle";
        } else {
            return null;
        }
    }

    public boolean enemigoVivo(Arma enemigo) {
        return enemigo.getVida() > 0;
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
        int tiempoRecarga = (int) (Math.round(daño * 0.2) * 10);
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