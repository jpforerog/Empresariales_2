package com.ProyectoEmpresariales.Arma.servicios;


import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Municion;

import java.util.ArrayList;
import java.util.List;

public class ServicioArma {

    private ArrayList<Arma> armas = new ArrayList<>();
    private int contador = 0;
    private static volatile ServicioArma instancia;

    private ServicioArma(){

    }

    public void añadirArma(Arma arm) throws Exception {
        if (arm != null ) {
            for (Arma arma : armas){
                if (arm.getNombre().equals(arma.getNombre()) && arm.getClass().equals(arma.getClass())){
                    throw new Exception("Arma con el mismo nombre y mismo tipo");
                }
            }
            arm.setIndex(contador);
            contador+=1;
            armas.add(arm);

        }
    }
    public static ServicioArma getInstancia() {
        if (instancia == null) {
            synchronized (ServicioMunicion.class) {
                if (instancia == null) {
                    instancia = new ServicioArma();
                }
            }
        }
        return instancia;
    }

    public void listarArma() {
        for (Arma arm : armas) {
            System.out.println(arm.toString());
        }
    }


    public List<Arma> getArmas() {
        return armas;
    }

    public void eliminarArma(Arma arma) {

        if (arma != null) {
            armas.remove(arma);
        }

    }

    public void actualizarArma(Arma armaAct, Arma nueva) {

        for (Arma ar : armas) {

            if (armaAct.equals(ar)) {

                int temp = ar.getIndex();
                armas.remove(ar);
                nueva.setIndex(temp);
                armas.add(nueva);
                return;
            }
        }


    }


}
