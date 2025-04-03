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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("/tipo")
    public ResponseEntity<?> getArmasTipo(@RequestBody JsonNode jsonNode) {
        objectMapper.registerModule(new JavaTimeModule());
        if (!jsonNode.has("tipo")) {
            return new ResponseEntity<>("El json tiene que tener tipo como parametro", HttpStatus.BAD_REQUEST);
        }

        String tipo = jsonNode.get("tipo").asText();

        if (tipo.equalsIgnoreCase("rifle")) {
            List<Arma> armas = servicioArma.getArmas().stream()
                    .filter(arma -> arma.getClass().toString().equals("class com.ProyectoEmpresariales.Arma.model.Rifle"))
                    .collect(Collectors.toList());

            if (armas.isEmpty()) {
                return new ResponseEntity<>("No hay armas de ese tipo", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("El tipo tiene que ser Rifle o Lanzador", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/vida")
    public ResponseEntity<?> getArmasVida(@RequestBody JsonNode jsonNode) {
        objectMapper.registerModule(new JavaTimeModule());
        if (!jsonNode.has("vida_minima")) {
            return new ResponseEntity<>("El json tiene que tener vida_minima como parametro", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("vida_minima").canConvertToInt()) {
            return new ResponseEntity<>("El valor tiene que ser un numero entero", HttpStatus.BAD_REQUEST);
        }

        int vidaMinima = jsonNode.get("vida_minima").asInt();
        List<Arma> armas = servicioArma.getArmas().stream()
                .filter(arma -> arma.getVida() >= vidaMinima)
                .collect(Collectors.toList());

        if (armas.isEmpty()) {
            return new ResponseEntity<>("No hay armas con esa vida minima", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
    }

    @PostMapping("/buscar")
    public ResponseEntity<?> getArmaIndice(@RequestBody JsonNode jsonNode) {
        objectMapper.registerModule(new JavaTimeModule());
        if (!jsonNode.has("indice")) {
            return new ResponseEntity<>("El json debe tener un atributo indice", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("indice").canConvertToInt()) {
            return new ResponseEntity<>("El valor del indice debe ser numerico", HttpStatus.BAD_REQUEST);
        }

        int indice = jsonNode.get("indice").asInt();

        if (!jsonNode.has("tipo")) {
            return new ResponseEntity<>("El json tiene que tener un atributo tipo", HttpStatus.BAD_REQUEST);
        }

        String tipo = jsonNode.get("tipo").asText();
        if (!tipo.equalsIgnoreCase("rifle")) {
            return new ResponseEntity<>("El tipo de arma debe ser rifle o lanzador", HttpStatus.BAD_REQUEST);
        }

        String tipoClase = "class com.ProyectoEmpresariales.Arma.model.Rifle";

        for (Arma arma : servicioArma.getArmas()) {
            if (arma.getIndex() == indice && arma.getClass().toString().equals(tipoClase)) {
                return new ResponseEntity<>(arma, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/buscarNombre")
    public ResponseEntity<?> getArma(@RequestBody JsonNode jsonNode) {
        objectMapper.registerModule(new JavaTimeModule());
        if (!jsonNode.has("nombre")) {
            return new ResponseEntity<>("El json debe tener un atributo nombre", HttpStatus.BAD_REQUEST);
        }

        String nombre = jsonNode.get("nombre").asText();

        for (Arma arma : servicioArma.getArmas()) {
            if (arma.getNombre().equals(nombre)) {
                return new ResponseEntity<>(arma, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<?> getArmaFilter(@RequestBody JsonNode jsonNode) {
        boolean tieneVidaMinima = jsonNode.has("vida_minima") && jsonNode.get("vida_minima").canConvertToInt();
        boolean tieneDañoMinimo = jsonNode.has("daño_minimo") && jsonNode.get("daño_minimo").canConvertToInt();

        if (!tieneVidaMinima && !tieneDañoMinimo) {
            return new ResponseEntity<>("El json debe tener al menos un filtro válido (vida_minima o daño_minimo)", HttpStatus.BAD_REQUEST);
        }

        List<Arma> armasFiltradas = new ArrayList<>();
        List<Arma> todasLasArmas = servicioArma.getArmas();

        // Primero filtramos todas las armas
        for (Arma arma : todasLasArmas) {
            boolean cumpleFiltros = true;

            if (tieneVidaMinima && arma.getVida() < jsonNode.get("vida_minima").asInt()) {
                cumpleFiltros = false;
            }

            if (tieneDañoMinimo && arma.getDaño() < jsonNode.get("daño_minimo").asInt()) {
                cumpleFiltros = false;
            }

            if (cumpleFiltros) {
                armasFiltradas.add(arma);
            }
        }

        if (armasFiltradas.isEmpty()) {
            return new ResponseEntity<>("No existen armas con esas características", HttpStatus.NOT_FOUND);
        }

        // Convertimos manualmente la lista a JSON para evitar problemas de conversión
        try {
            String jsonResponse = objectMapper.writeValueAsString(armasFiltradas);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al convertir los resultados a JSON: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String verificarCamposYTipos(JsonNode jsonNode) {
        // Verificar existencia y tipo de cada campo
        if (!jsonNode.has("nombre") || !jsonNode.get("nombre").isTextual()) {
            return new String("El nombre tiene que ser un texto");
        }

        if (!jsonNode.has("daño") || !jsonNode.get("daño").isNumber()) {
            return new String("El daño tiene que ser un entero");
        }

        if (!jsonNode.has("municion") || !jsonNode.get("municion").isNumber()) {
            return new String("la municion tiene que ser un entero");
        }

        if (!jsonNode.has("vida") || !jsonNode.get("vida").isNumber()) {
            return new String("La vida tiene que ser un entero");
        }

        if (!jsonNode.has("velocidad") || !jsonNode.get("velocidad").isNumber()) {
            return new String("La velocidad tiene que ser un numero");
        }

        if (!jsonNode.has("fechaCreacion") || !jsonNode.get("fechaCreacion").isTextual()) {
            return new String("La fecha de creacion tiene que tener este formato [0000-00-00T00:00:00,Año-mes-diaTHora,Minutos,Sg]");
        }

        return "json valido";
    }

    @PostMapping(value = "/")
    //Revisar que no entren jsons nulos
    public ResponseEntity añadirRifle(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        String res = verificarCamposYTipos(jsonNode);
        if(!res.equals("json valido")){
            return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
        }


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
        objectMapper.registerModule(new JavaTimeModule());
        try {
            servicioArma.añadirArma(new Rifle(2,2,"hola",2,2,LocalDateTime.parse("2025-03-28T00:00:10")));
        } catch (Exception e) {
            ResponseEntity.internalServerError().body(e.toString());
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(value = "/")
    public ResponseEntity eliminarArma(@RequestBody JsonNode jsonNode){
        objectMapper.registerModule(new JavaTimeModule());
        int index = -1;

        if (!jsonNode.has("indice")){
            return new ResponseEntity("Tienes que poner el campo indice con el que esta identificado el arma",HttpStatus.BAD_REQUEST);
        }

        if (jsonNode.get("indice").canConvertToInt()){
            index = jsonNode.get("indice").asInt();
        }else {
            return new ResponseEntity("El indice tiene que ser un numero entero",HttpStatus.BAD_REQUEST);
        }

        String tipo = jsonNode.get("tipo").asText();
        if (tipo.equalsIgnoreCase("Rifle")){
            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }else {
            return new ResponseEntity("El tipo de arma debe ser rifle o lanzador",HttpStatus.BAD_REQUEST);
        }

        for (Arma arma: servicioArma.getArmas()){

            if(arma.getIndex() == index && String.valueOf(arma.getClass()).equals(tipo)){

                servicioArma.eliminarArma(arma);
                return new ResponseEntity<>(arma,HttpStatus.ACCEPTED);

            }
        }
        return new ResponseEntity("Arma no encontrada",HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/")
    public ResponseEntity actualizarArma(@RequestBody JsonNode jsonNode){
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
        if (jsonNode.get("tipo").asText().equals("Rifle")){

            tipo="class com.ProyectoEmpresariales.Arma.model.Rifle";
        }else{
            ResponseEntity.badRequest().body("Tipo de arma no encontrado");
        }

        for (Arma arma: servicioArma.getArmas()){

            if(arma.getIndex() == jsonNode.get("indice").asInt() && String.valueOf(arma.getClass()).equals(tipo) && !jsonNode.get("nombre").asText().isEmpty()){

                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove("tipo");
                objectNode.remove("indice");

                String nom = jsonNode.get("nombre").asText();
                for(Arma a : servicioArma.getArmas()){
                    if(arma.getIndex()!=a.getIndex() && nom.equals(a.getNombre())){
                        return new ResponseEntity<>("Otra arma con el mimo nombre ya fue creada",HttpStatus.BAD_REQUEST);
                    }
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

}
