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

//    @GetMapping("/tipo")
//    public ResponseEntity<?> getArmasTipo(@RequestBody JsonNode jsonNode) {
//        objectMapper.registerModule(new JavaTimeModule());
//        if (!jsonNode.has("tipo")) {
//            return new ResponseEntity<>("El json tiene que tener tipo como parametro", HttpStatus.BAD_REQUEST);
//        }
//
//        String tipo = jsonNode.get("tipo").asText();
//
//        if (tipo.equalsIgnoreCase("rifle")) {
//            List<Arma> armas = servicioArma.getArmas().stream()
//                    .filter(arma -> arma.getClass().toString().equals("class com.ProyectoEmpresariales.Arma.model.Rifle"))
//                    .collect(Collectors.toList());
//
//            if (armas.isEmpty()) {
//                return new ResponseEntity<>("No hay armas de ese tipo", HttpStatus.NOT_FOUND);
//            }
//
//            return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("El tipo tiene que ser Rifle o Lanzador", HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @GetMapping("/vida")
//    public ResponseEntity<?> getArmasVida(@RequestBody JsonNode jsonNode) {
//        objectMapper.registerModule(new JavaTimeModule());
//        if (!jsonNode.has("vida_minima")) {
//            return new ResponseEntity<>("El json tiene que tener vida_minima como parametro", HttpStatus.BAD_REQUEST);
//        }
//
//        if (!jsonNode.get("vida_minima").canConvertToInt()) {
//            return new ResponseEntity<>("El valor tiene que ser un numero entero", HttpStatus.BAD_REQUEST);
//        }
//
//        int vidaMinima = jsonNode.get("vida_minima").asInt();
//        List<Arma> armas = servicioArma.getArmas().stream()
//                .filter(arma -> arma.getVida() >= vidaMinima)
//                .collect(Collectors.toList());
//
//        if (armas.isEmpty()) {
//            return new ResponseEntity<>("No hay armas con esa vida minima", HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
//    }
//
//    @GetMapping("/buscar")
//    public ResponseEntity<?> getArmaIndice(@RequestBody JsonNode jsonNode) {
//        objectMapper.registerModule(new JavaTimeModule());
//        if (!jsonNode.has("indice")) {
//            return new ResponseEntity<>("El json debe tener un atributo indice", HttpStatus.BAD_REQUEST);
//        }
//
//        if (!jsonNode.get("indice").canConvertToInt()) {
//            return new ResponseEntity<>("El valor del indice debe ser numerico", HttpStatus.BAD_REQUEST);
//        }
//
//        int indice = jsonNode.get("indice").asInt();
//
//        if (!jsonNode.has("tipo")) {
//            return new ResponseEntity<>("El json tiene que tener un atributo tipo", HttpStatus.BAD_REQUEST);
//        }
//
//        String tipo = jsonNode.get("tipo").asText();
//        if (!tipo.equalsIgnoreCase("rifle")) {
//            return new ResponseEntity<>("El tipo de arma debe ser rifle o lanzador", HttpStatus.BAD_REQUEST);
//        }
//
//        String tipoClase = "class com.ProyectoEmpresariales.Arma.model.Rifle";
//
//        for (Arma arma : servicioArma.getArmas()) {
//            if (arma.getIndex() == indice && arma.getClass().toString().equals(tipoClase)) {
//                return new ResponseEntity<>(arma, HttpStatus.OK);
//            }
//        }
//
//        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
//    }
//
//    @GetMapping("/buscarNombre")
//    public ResponseEntity<?> getArma(@RequestBody JsonNode jsonNode) {
//        objectMapper.registerModule(new JavaTimeModule());
//        if (!jsonNode.has("nombre")) {
//            return new ResponseEntity<>("El json debe tener un atributo nombre", HttpStatus.BAD_REQUEST);
//        }
//
//        String nombre = jsonNode.get("nombre").asText();
//
//        for (Arma arma : servicioArma.getArmas()) {
//            if (arma.getNombre().equals(nombre)) {
//                return new ResponseEntity<>(arma, HttpStatus.OK);
//            }
//        }
//
//        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
//    }
//
//    @GetMapping("/filtrar")
//    public ResponseEntity<?> getArmaFilter(@RequestBody JsonNode jsonNode) {
//        boolean tieneVidaMinima = jsonNode.has("vida_minima") && jsonNode.get("vida_minima").canConvertToInt();
//        boolean tieneDañoMinimo = jsonNode.has("dano_minimo") && jsonNode.get("dano_minimo").canConvertToInt();
//
//        if (!tieneVidaMinima && !tieneDañoMinimo) {
//            return new ResponseEntity<>("El json debe tener al menos un filtro válido (vida_minima o dano_minimo)", HttpStatus.BAD_REQUEST);
//        }
//
//        List<Arma> armasFiltradas = new ArrayList<>();
//        List<Arma> todasLasArmas = servicioArma.getArmas();
//
//        // Primero filtramos todas las armas
//        for (Arma arma : todasLasArmas) {
//            boolean cumpleFiltros = true;
//
//            if (tieneVidaMinima && arma.getVida() < jsonNode.get("vida_minima").asInt()) {
//                cumpleFiltros = false;
//            }
//
//            if (tieneDañoMinimo && arma.getDaño() < jsonNode.get("dano_minimo").asInt()) {
//                cumpleFiltros = false;
//            }
//
//            if (cumpleFiltros) {
//                armasFiltradas.add(arma);
//            }
//        }
//
//        if (armasFiltradas.isEmpty()) {
//            return new ResponseEntity<>("No existen armas con esas características", HttpStatus.NOT_FOUND);
//        }
//
//        // Convertimos manualmente la lista a JSON para evitar problemas de conversión
//        try {
//            String jsonResponse = objectMapper.writeValueAsString(armasFiltradas);
//            return ResponseEntity.ok()
//                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
//                    .body(jsonResponse);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error al convertir los resultados a JSON: " + e.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public String verificarCamposYTipos(JsonNode jsonNode) {
//        // Verificar existencia y tipo de cada campo
//        if (!jsonNode.has("nombre") || !jsonNode.get("nombre").isTextual()) {
//            return new String("El nombre tiene que ser un texto");
//        }
//
//        if (!jsonNode.has("daño") || !jsonNode.get("daño").isNumber()) {
//            return new String("El daño tiene que ser un entero");
//        }
//
//        if (!jsonNode.has("municion") || !jsonNode.get("municion").isNumber()) {
//            return new String("la municion tiene que ser un entero");
//        }
//
//        if (!jsonNode.has("vida") || !jsonNode.get("vida").isNumber()) {
//            return new String("La vida tiene que ser un entero");
//        }
//
//        if (!jsonNode.has("velocidad") || !jsonNode.get("velocidad").isNumber()) {
//            return new String("La velocidad tiene que ser un numero");
//        }
//
//        if (!jsonNode.has("fechaCreacion") || !jsonNode.get("fechaCreacion").isTextual()) {
//            return new String("La fecha de creacion tiene que tener este formato [0000-00-00T00:00:00,Año-mes-diaTHora,Minutos,Sg]");
//        }
//
//        return "json valido";
//    }

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
            index = jsonNode.get("indice").asInt();
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
