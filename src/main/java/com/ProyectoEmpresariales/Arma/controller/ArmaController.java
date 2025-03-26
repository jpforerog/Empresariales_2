package com.ProyectoEmpresariales.Arma.controller;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Arma")

public class ArmaController {
    ServicioArma servicioArma = new ServicioArma();
    @GetMapping(value = "/healthCheck")
    public String healthCheck(){
        return "service status OK!";
    }
    @GetMapping(value = "/")
    public List<Arma> getArmas(){

        return servicioArma.getArmas();
    }
    @PostMapping(value = "/set")
    //Revisar que no entren jsons nulos
    public void añadirRifle(@RequestBody Rifle rifle){
        if(null != rifle){
            servicioArma.añadirArma(rifle);
            System.out.println("Arma añadida correctamente");

        }

    }
    @DeleteMapping(value = "/")
    //no es igual la entrada
    public List<Arma> eliminarRifle(@RequestBody Rifle rifle){
        Arma rifleTemp = rifle;
        List<Arma> lista = servicioArma.getArmas();
        Arma x = lista.get(1);
        System.out.println(x);
        System.out.println(rifleTemp == x);
        System.out.println("2");
        System.out.println(rifleTemp.equals(x));
        if(x == rifle){
            System.out.println("son iguales");
        }
        servicioArma.eliminarArma(rifle);
        return servicioArma.getArmas();
    }


}
