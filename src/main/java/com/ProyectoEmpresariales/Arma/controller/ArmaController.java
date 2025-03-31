package com.ProyectoEmpresariales.Arma.controller;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
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
    public ResponseEntity getArmas(){
        objectMapper.registerModule(new JavaTimeModule());
        ArrayNode arrayNode = objectMapper.valueToTree(servicioArma.getArmas());
        if(arrayNode.isEmpty()){
            return new ResponseEntity<>("No hay armas",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(arrayNode,HttpStatus.OK);
    }
    @GetMapping(value = "/tipo")
    public ResponseEntity getArmasTipo(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        ArrayList<Arma> armas = new ArrayList<>();

        if(!jsonNode.has("tipo")){
            return new ResponseEntity<>("El json tiene que tener tipo como parametro",HttpStatus.BAD_REQUEST);
        }

        if(jsonNode.get("tipo").isTextual() && jsonNode.get("tipo").asText().equalsIgnoreCase("rifle")){
            for (Arma arma : servicioArma.getArmas()){
                System.out.println(arma.getClass());
                if(arma.getClass().toString().equals("class com.ProyectoEmpresariales.Arma.model.Rifle")){
                    armas.add(arma);
                }
            }
        }else {
            return new ResponseEntity<>("El tipo tiene que ser Rifle o Lanzador",HttpStatus.BAD_REQUEST);
        }
        ArrayNode arrayNode = objectMapper.valueToTree(armas);
        if(arrayNode.isEmpty()){
            return new ResponseEntity<>("No hay armas de ese tipo",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(arrayNode,HttpStatus.OK);
    }

    @GetMapping(value = "/vida")
    public ResponseEntity getArmasVida(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        ArrayList<Arma> armas = new ArrayList<>();
        if(!jsonNode.has("vida minima")){
            return new ResponseEntity<>("El json tiene que tener vida minima como parametro",HttpStatus.BAD_REQUEST);
        }

        if(jsonNode.get("vida minima").canConvertToInt()) {
            for (Arma arma : servicioArma.getArmas()) {

                if (arma.getVida() >= jsonNode.get("vida minima").asInt()) {
                    armas.add(arma);
                }
            }
        }else{
            return new ResponseEntity<>("El valor tiene que ser un numero entero",HttpStatus.BAD_REQUEST);
        }
        ArrayNode arrayNode = objectMapper.valueToTree(armas);
        if(arrayNode.isEmpty()){
            return new ResponseEntity<>("No hay armas con esa vida minima",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(arrayNode,HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/buscar")
    public ResponseEntity getArma(@RequestBody JsonNode jsonNode){
        String nombre = jsonNode.get("nombre").asText();
        String tipo = "";
        if(jsonNode.get("tipo").asText().equalsIgnoreCase("rifle")){
            tipo = "class com.ProyectoEmpresariales.Arma.model.Rifle";
        }
        for(Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre) && arma.getClass().toString().equals(tipo)){
                System.out.println(arma);
                return new ResponseEntity<>(arma,HttpStatus.BAD_REQUEST);
            }else {
                ResponseEntity.notFound();
            }
        }
        return new ResponseEntity<>("Arma no encontrada",HttpStatus.NOT_FOUND);
    }
    @PostMapping(value = "/")
    //Revisar que no entren jsons nulos
    public ResponseEntity añadirRifle(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        if(!jsonNode.get("nombre").asText().isEmpty()){
            try {
                Rifle rifle = objectMapper.treeToValue(jsonNode, Rifle.class);
                Arma rifle1 = (Arma) rifle;
                servicioArma.añadirArma(rifle1);
                return new ResponseEntity<>(rifle1,HttpStatus.ACCEPTED);
            }catch (Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }
        }
            return ResponseEntity.badRequest().body("Arma sin nombre");

    }
    //Solo pruebas
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
    public ResponseEntity eliminarArma(@RequestBody JsonNode jsonNode){

        Arma arma1 = servicioArma.getArmas().get(0);
        String nombre = jsonNode.get("nombre").asText();
        String tipo = jsonNode.get("tipo").asText();
        if (tipo.equalsIgnoreCase("Rifle")){
            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }

        for (Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre)&&String.valueOf(arma.getClass()).equals(tipo)){

                servicioArma.eliminarArma(arma);
                return new ResponseEntity<>(arma,HttpStatus.ACCEPTED);

            }
        }
        return new ResponseEntity("Arma no encontrada",HttpStatus.NOT_FOUND);
    }
    @PutMapping(value = "/")
    public ResponseEntity actualizarArma(@RequestBody JsonNode jsonNode){
        String nombre ="";

        if(!jsonNode.get("nombre").asText().isEmpty()) {

            nombre = jsonNode.get("nombre").asText();
        }else{
            System.out.println("nulo");
            ResponseEntity.badRequest().body("Nombre de arma nulo");
        }
        String tipo = "";
        if (jsonNode.get("tipo").asText().equals("Rifle")){

            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }else{
            ResponseEntity.badRequest().body("Tipo de arma no encontrado");
        }

        for (Arma arma: servicioArma.getArmas()){

            if(arma.getNombre().equals(nombre) && String.valueOf(arma.getClass()).equals(tipo) && !jsonNode.get("nombre").asText().isEmpty()){

                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove("tipo");
                if(jsonNode.get("nombreNuevo").asText().isEmpty()){
                    objectNode.remove("nombreNuevo");
                }else {
                    String n = jsonNode.get("nombreNuevo").asText();
                    objectNode.put("nombre",n);
                    objectNode.remove("nombreNuevo");
                }
                JsonNode json = (JsonNode) objectNode;
                try {
                    Rifle armaAct = objectMapper.treeToValue(json, Rifle.class);
                    Arma armaAct1 = (Arma) armaAct;
                    servicioArma.actualizarArma(arma,armaAct1);
                    return new ResponseEntity<>(armaAct1,HttpStatus.ACCEPTED);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }





            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.BAD_REQUEST);
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
