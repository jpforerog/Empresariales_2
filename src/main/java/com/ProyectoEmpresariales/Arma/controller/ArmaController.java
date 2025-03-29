package com.ProyectoEmpresariales.Arma.controller;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/Arma")


public class ArmaController {
    ServicioArma servicioArma = new ServicioArma();
    ObjectMapper objectMapper = new ObjectMapper();




    @GetMapping(value = "/healthCheck")
    public String healthCheck(){
        objectMapper.registerModule(new JavaTimeModule());
        return "service status OK!";
    }
    @GetMapping(value = "/")
    public ArrayNode getArmas(){
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.valueToTree(servicioArma.getArmas());
    }
    @GetMapping(value = "/buscar")
    public Arma getArma(@RequestBody JsonNode jsonNode){
        String nombre = jsonNode.get("nombre").asText();

        for(Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre)){
                System.out.println(arma);
                return arma;
            }else {
                ResponseEntity.notFound();
            }
        }
        return null;
    }
    @PostMapping(value = "/")
    //Revisar que no entren jsons nulos
    public ResponseEntity<String> añadirRifle(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        if(!jsonNode.get("nombre").asText().isEmpty()){
            try {
                Rifle rifle = objectMapper.treeToValue(jsonNode, Rifle.class);
                Arma rifle1 = (Arma) rifle;
                servicioArma.añadirArma(rifle1);
                return ResponseEntity.ok("Arma agregada correctamente");
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Error: Datos del arma incompletos");
            }
        }
            return ResponseEntity.badRequest().body("Arma sin nombre");

    }
    @GetMapping(value = "")
    public void test(){
        try {
            servicioArma.añadirArma(new Rifle(2,2,"hola",2,2,LocalDateTime.parse("2025-03-28T00:00:10")));
        } catch (Exception e) {
            ResponseEntity.internalServerError().body(e.toString());
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping(value = "/")
    public List<Arma> eliminarArma(@RequestBody JsonNode jsonNode){

        Arma arma1 = servicioArma.getArmas().get(0);
        String nombre = jsonNode.get("nombre").asText();
        String tipo = jsonNode.get("tipo").asText();
        if (tipo.equals("Rifle")){
            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }
        for (Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre)&&String.valueOf(arma.getClass()).equals(tipo)){
                System.out.println(servicioArma.getArmas());
                servicioArma.eliminarArma(arma);
                System.out.println(servicioArma.getArmas());
                return servicioArma.getArmas();

            }
        }return servicioArma.getArmas();
    }
    @PutMapping(value = "/")
    public Arma actualizarArma(@RequestBody JsonNode jsonNode){
        Arma arma1 = servicioArma.getArmas().get(0);
        String nombre = jsonNode.get("nombre").asText();
        String tipo = jsonNode.get("tipo").asText();
        if (tipo.equals("Rifle")){
            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }
        for (Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre) && String.valueOf(arma.getClass()).equals(tipo)){
                System.out.println(servicioArma.getArmas());
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove("tipo");
                JsonNode json = (JsonNode) objectNode;
                try {
                    Rifle armaAct = objectMapper.treeToValue(json, Rifle.class);
                    Arma armaAct1 = (Arma) armaAct;
                    servicioArma.actualizarArma(arma,armaAct1);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }


                System.out.println(servicioArma.getArmas());
                return arma;

            }
        }return null;
    }
    /*@DeleteMapping(value = "/")
    //no es igual la entrada
    public List<Arma> eliminarRifle(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        try{
            Rifle arma = objectMapper.treeToValue(jsonNode, Rifle.class);


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            ResponseEntity.notFound();
            throw new RuntimeException(e);
        }


        return servicioArma.getArmas();
    }*/


}
