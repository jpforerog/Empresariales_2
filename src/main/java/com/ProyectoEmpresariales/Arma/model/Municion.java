package com.ProyectoEmpresariales.Arma.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Municion {
    private String nombre;
    private boolean dañoArea;
    private int cadencia;
    private int index;
    public Municion(@JsonProperty("nombre") String nombre,@JsonProperty("danoArea") boolean dañoArea,@JsonProperty("cadencia") int cadencia){
        this.nombre = nombre;
        this.dañoArea = dañoArea;
        this.cadencia = cadencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDañoArea() {
        return dañoArea;
    }

    public void setDañoArea(boolean dañoArea) {
        this.dañoArea = dañoArea;
    }

    public int getCadencia() {
        return cadencia;
    }

    public void setCadencia(int cadencia) {
        this.cadencia = cadencia;
    }
}
