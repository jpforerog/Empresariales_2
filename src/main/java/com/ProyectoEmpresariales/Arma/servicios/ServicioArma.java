package com.ProyectoEmpresariales.Arma.servicios;



import com.ProyectoEmpresariales.Arma.model.Arma;
import java.util.ArrayList;
import java.util.List;

public class ServicioArma {

    private ArrayList<Arma> armas = new ArrayList<>();


    public void añadirArma(Arma arm) {
        if (arm != null) {
            armas.add(arm);

        }
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
            System.out.println(armaAct.equals(ar));
            if (armaAct.equals(ar)) {
                System.out.println("Entraaa");
                System.out.println(nueva);
                armas.remove(ar);
                armas.add(nueva);

                return;
            }
        }


    }


}
