package com.ProyectoEmpresariales.Arma.servicios;


import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Municion;
import com.ProyectoEmpresariales.Arma.model.Rifle;

import java.util.ArrayList;
import java.util.List;

public class ServicioMunicion {

    private static volatile ServicioMunicion instancia;
    private ArrayList<Municion> municiones = new ArrayList<>();
    private int contador = 0;
    private ServicioArma servicioArma= ServicioArma.getInstancia();

    private ServicioMunicion(){
        try {
            añadirMunicion(Municion.builder()
                    .nombre("Predeterminado")
                    .dañoArea(false)
                    .cadencia(10)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        };
    }

    public static ServicioMunicion getInstancia() {
        if (instancia == null) {
            synchronized (ServicioMunicion.class) {
                if (instancia == null) {
                    instancia = new ServicioMunicion();
                }
            }
        }
        return instancia;
    }

    public void añadirMunicion(Municion arm) throws Exception {
        if (arm != null ) {
            for (Municion arma : municiones){
                if (arm.getNombre().equals(arma.getNombre())){
                    throw new Exception("Municion con el mismo nombre");
                }
            }
            arm.setIndex(contador);
            contador+=1;
            municiones.add(arm);

        }
    }

    public void listarMuniciones() {
        for (Municion arm : municiones) {
            System.out.println(arm.toString());
        }
    }


    public List<Municion> getMuniciones() {
        return municiones;
    }

    public void eliminarMunicion(Municion arma) {

        if (arma != null) {
            municiones.remove(arma);
            cambiarPredeterminada(arma);
        }


    }
    void cambiarPredeterminada(Municion arma){
        for (Arma armaTemp : servicioArma.getArmas()){
            if (arma.getIndex()==((Rifle) armaTemp).getTipoMunicion().getIndex()){
                ((Rifle) armaTemp).setTipoMunicion(getPredeterminada());
            }
        }
    }

    public void actualizarMunicion(Municion armaAct, Municion nueva) {

        for (Municion ar : municiones) {

            if (armaAct.equals(ar)) {

                int temp = ar.getIndex();
                municiones.remove(ar);
                nueva.setIndex(temp);
                municiones.add(nueva);
                actualizarMunicionArma(nueva);
                return;
            }
        }


    }

    void actualizarMunicionArma(Municion municion){
        for(Arma arma: servicioArma.getArmas()){
            if(municion.getIndex() == ((Rifle)arma).getTipoMunicion().getIndex()){
                ((Rifle) arma).setTipoMunicion(municion);
            }
        }
    }
    public Municion getPredeterminada(){
        for (Municion mun : municiones){
            if(mun.getIndex()==0){
                return mun;
            }
        }return null;
    }


}
