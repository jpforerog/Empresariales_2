package com.ProyectoEmpresariales.Arma.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "MUNICIONES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municion_seq")
    @SequenceGenerator(name = "municion_seq", sequenceName = "MUNICION_SEQ", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(name = "DANO_AREA")
    private boolean dañoArea;

    @Column(nullable = false)
    private int cadencia;

    @OneToMany(mappedBy = "tipoMunicion")
    @JsonManagedReference
    private List<Rifle> rifles;

    public Municion(@JsonProperty("nombre") String nombre,
                    @JsonProperty("danoArea") boolean dañoArea,
                    @JsonProperty("cadencia") int cadencia) {
        this.nombre = nombre;
        this.dañoArea = dañoArea;
        this.cadencia = cadencia;
    }
}