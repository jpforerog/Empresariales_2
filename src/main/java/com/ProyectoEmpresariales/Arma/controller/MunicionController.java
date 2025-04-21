package com.ProyectoEmpresariales.Arma.controller;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Municion;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import com.ProyectoEmpresariales.Arma.servicios.ServicioMunicion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Municion")


public class MunicionController {
    ServicioArma servicioArma = ServicioArma.getInstancia();
    ServicioMunicion servicioMunicion = ServicioMunicion.getInstancia();
    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/healthCheck")
    public String healthCheck(){
        objectMapper.registerModule(new JavaTimeModule());
        return "service status OK!";
    }

    @GetMapping(value = "/")
    public ResponseEntity getMuniciones(){
        objectMapper.registerModule(new JavaTimeModule());
        ArrayNode arrayNode = objectMapper.valueToTree(servicioMunicion.getMuniciones());
        if(arrayNode.isEmpty()){
            return new ResponseEntity<>("No hay municiones",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(arrayNode,HttpStatus.OK);
    }

    @GetMapping(value = "/buscarNombre/")
    public ResponseEntity getPorNombre(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        if(!jsonNode.has("nombre")){
            return new ResponseEntity("Falta nombre",HttpStatus.BAD_REQUEST);
        }

        for(Municion municion:servicioMunicion.getMuniciones()){
            if(municion.getNombre().equals(jsonNode.get("nombre").asText())){
                return new ResponseEntity(municion,HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity("Municion no encontrada",HttpStatus.NOT_FOUND);
    }
    @PostMapping(value = "/buscarNombre/")
    public ResponseEntity getPorNombre1(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        if(!jsonNode.has("nombre")){
            return new ResponseEntity("Falta nombre",HttpStatus.BAD_REQUEST);
        }

        for(Municion municion:servicioMunicion.getMuniciones()){
            if(municion.getNombre().equals(jsonNode.get("nombre").asText())){
                return new ResponseEntity(municion,HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity("Municion no encontrada",HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/buscar/")
    public ResponseEntity getPorID(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());

        for(Municion municion:servicioMunicion.getMuniciones()){
            if(municion.getIndex() == (jsonNode.get("indice").asInt())){
                return new ResponseEntity(municion,HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity("Municion no encontrada",HttpStatus.NOT_FOUND);
    }
    @PostMapping(value = "/buscar/")
    public ResponseEntity getPorID1(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());

        for(Municion municion:servicioMunicion.getMuniciones()){
            if(municion.getIndex() == (jsonNode.get("indice").asInt())){
                return new ResponseEntity(municion,HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity("Municion no encontrada",HttpStatus.NOT_FOUND);
    }

    @PostMapping("/filtrarMunicion")
    public ResponseEntity<?> getMunicionFilter(@RequestBody JsonNode jsonNode) {
        boolean tieneCadenciaMinima = jsonNode.has("cadencia_minima") && jsonNode.get("cadencia_minima").canConvertToInt();
        boolean tieneDañoArea = jsonNode.has("danoArea") && jsonNode.get("danoArea").isBoolean();

        if (!tieneCadenciaMinima && !tieneDañoArea) {
            return new ResponseEntity<>("El json debe tener al menos un filtro válido (cadencia_minima o danoArea)", HttpStatus.BAD_REQUEST);
        }

        List<Municion> municionesFiltradas = new ArrayList<>();
        List<Municion> todasLasMuniciones = servicioMunicion.getMuniciones(); // Asumiendo que existe este servicio

        // Filtramos todas las municiones
        for (Municion municion : todasLasMuniciones) {
            boolean cumpleFiltros = true;

            if (tieneCadenciaMinima && municion.getCadencia() < jsonNode.get("cadencia_minima").asInt()) {
                cumpleFiltros = false;
            }

            if (tieneDañoArea && municion.isDañoArea() != jsonNode.get("danoArea").asBoolean()) {
                cumpleFiltros = false;
            }

            if (cumpleFiltros) {
                municionesFiltradas.add(municion);
            }
        }

        if (municionesFiltradas.isEmpty()) {
            return new ResponseEntity<>("No existen municiones con esas características", HttpStatus.NOT_FOUND);
        }

        // Convertimos manualmente la lista a JSON para evitar problemas de conversión
        try {
            String jsonResponse = objectMapper.writeValueAsString(municionesFiltradas);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al convertir los resultados a JSON: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/")
    public ResponseEntity añadirMunicion(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());

        if(!jsonNode.get("nombre").asText().isEmpty() && jsonNode.get("cadencia").canConvertToInt()){
            try {
                Municion municion = objectMapper.treeToValue(jsonNode, Municion.class);
                servicioMunicion.añadirMunicion(municion);
                return new ResponseEntity<>(municion,HttpStatus.ACCEPTED);
            }catch (Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }
        }
            return new ResponseEntity<>("Municion sin nombre o sin cadencia",HttpStatus.BAD_REQUEST);

    }


    @DeleteMapping(value = "/")
    public ResponseEntity eliminarMunicion(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        int index = -1;

        if (!jsonNode.has("indice")){
            return new ResponseEntity("Tienes que poner el campo indice con el que esta identificado el arma",HttpStatus.BAD_REQUEST);
        }

        if (jsonNode.get("indice").canConvertToInt()){
            if(jsonNode.get("indice").asInt() != 0){
                index = jsonNode.get("indice").asInt();
            }else{
                return new ResponseEntity<>("La municion predeterminada no se puede eliminar",HttpStatus.BAD_REQUEST);
            }

        }else {
            return new ResponseEntity("El indice tiene que ser un numero entero",HttpStatus.BAD_REQUEST);
        }



        for (Municion municion: servicioMunicion.getMuniciones()){

            if(municion.getIndex() == index ){

                servicioMunicion.eliminarMunicion(municion);
                return new ResponseEntity<>(municion,HttpStatus.ACCEPTED);

            }
        }
        return new ResponseEntity("municion no encontrada",HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/")
    public ResponseEntity actualizarMunicion(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        String nombre ="";
        if(!jsonNode.has("indice")){
            return new ResponseEntity("Ingresa el campo de indice",HttpStatus.BAD_REQUEST);
        }
        if(!jsonNode.get("nombre").asText().isEmpty()) {
            nombre = jsonNode.get("nombre").asText();
        }else{
            System.out.println("nulo");
            ResponseEntity.badRequest().body("Nombre de arma nulo");
        }
        String tipo = "";


        for (Municion municion: servicioMunicion.getMuniciones()){

            if(municion.getIndex() == jsonNode.get("indice").asInt()  && !jsonNode.get("nombre").asText().isEmpty()){

                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove("indice");

                String nom = jsonNode.get("nombre").asText();
                for(Municion a : servicioMunicion.getMuniciones()){
                    if(municion.getIndex()!=a.getIndex() && nom.equals(a.getNombre())){
                        return new ResponseEntity<>("Otra municion con el mimo nombre ya fue creada",HttpStatus.BAD_REQUEST);
                    }
                }
                JsonNode json = (JsonNode) objectNode;
                try {
                    Municion munAct = objectMapper.treeToValue(json, Municion.class);

                    servicioMunicion.actualizarMunicion(municion,munAct);
                    return new ResponseEntity<>(munAct,HttpStatus.ACCEPTED);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }





            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.BAD_REQUEST);
    }

}
