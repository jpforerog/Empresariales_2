package com.ProyectoEmpresariales.Arma.controller;

import com.ProyectoEmpresariales.Arma.model.Arma;
import com.ProyectoEmpresariales.Arma.model.Municion;
import com.ProyectoEmpresariales.Arma.model.Rifle;
import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import com.ProyectoEmpresariales.Arma.servicios.ServicioMunicion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Arma")
public class ArmaController {

    @Autowired
    private ServicioArma servicioArma;

    @Autowired
    private ServicioMunicion servicioMunicion;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GetMapping(value = "/healthCheck")
    public String healthCheck() {
        return "service status OK!";
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> getArmas() {
        List<Arma> armas = servicioArma.getArmas();

        if (armas.isEmpty()) {
            return new ResponseEntity<>("No hay armas", HttpStatus.NOT_FOUND);
        }

        ArrayNode arrayNode = objectMapper.valueToTree(armas);
        return new ResponseEntity<>(arrayNode, HttpStatus.OK);
    }

    @GetMapping("/tipo")
    public ResponseEntity<?> getArmasTipo(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("tipo")) {
            return new ResponseEntity<>("El json tiene que tener tipo como parametro", HttpStatus.BAD_REQUEST);
        }

        String tipo = jsonNode.get("tipo").asText();

        if (tipo.equalsIgnoreCase("rifle")) {
            List<Arma> armas = servicioArma.findByTipo("rifle");

            if (armas.isEmpty()) {
                return new ResponseEntity<>("No hay armas de ese tipo", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("El tipo tiene que ser Rifle o Lanzador", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/vida")
    public ResponseEntity<?> getArmasVida(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("vida_minima")) {
            return new ResponseEntity<>("El json tiene que tener vida_minima como parametro", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("vida_minima").isInt()) {
            return new ResponseEntity<>("El valor tiene que ser un numero entero", HttpStatus.BAD_REQUEST);
        }

        int vidaMinima = jsonNode.get("vida_minima").asInt();
        List<Arma> armas = servicioArma.findByVidaMinima(vidaMinima);

        if (armas.isEmpty()) {
            return new ResponseEntity<>("No hay armas con esa vida minima", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(objectMapper.valueToTree(armas), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getArmaIndice(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("indice")) {
            return new ResponseEntity<>("El json debe tener un atributo indice", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("indice").isInt()) {
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

        // Buscar entre los rifles
        List<Rifle> rifles = servicioArma.getRifles();
        for (Rifle rifle : rifles) {
            if (rifle.getIndex() == indice) {
                return new ResponseEntity<>(rifle, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/buscarNombre")
    public ResponseEntity<?> getArma(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("nombre")) {
            return new ResponseEntity<>("El json debe tener un atributo nombre", HttpStatus.BAD_REQUEST);
        }

        String nombre = jsonNode.get("nombre").asText();
        Optional<Arma> armaOpt = servicioArma.findByNombre(nombre);

        if (armaOpt.isPresent()) {
            return new ResponseEntity<>(armaOpt.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/buscar")
    public ResponseEntity<?> getArmaIndice1(@RequestBody JsonNode jsonNode) {
        return getArmaIndice(jsonNode);
    }

    @PostMapping("/buscarNombre")
    public ResponseEntity<?> getArma1(@RequestBody JsonNode jsonNode) {
        return getArma(jsonNode);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<?> getArmaFilter(@RequestBody JsonNode jsonNode) {
        boolean tieneVidaMinima = jsonNode.has("vida_minima") && jsonNode.get("vida_minima").isInt();
        boolean tieneDañoMinimo = jsonNode.has("dano_minimo") && jsonNode.get("dano_minimo").isInt();

        if (!tieneVidaMinima && !tieneDañoMinimo) {
            return new ResponseEntity<>("El json debe tener al menos un filtro válido (vida_minima o dano_minimo)",
                    HttpStatus.BAD_REQUEST);
        }

        List<Arma> armasFiltradas = new ArrayList<>();

        // Aplicar filtros
        if (tieneVidaMinima && tieneDañoMinimo) {
            // Filtrar por ambos criterios
            int vidaMinima = jsonNode.get("vida_minima").asInt();
            int dañoMinimo = jsonNode.get("dano_minimo").asInt();

            List<Arma> armasPorVida = servicioArma.findByVidaMinima(vidaMinima);
            armasFiltradas = armasPorVida.stream()
                    .filter(arma -> arma.getDaño() >= dañoMinimo)
                    .collect(Collectors.toList());
        } else if (tieneVidaMinima) {
            // Filtrar solo por vida mínima
            int vidaMinima = jsonNode.get("vida_minima").asInt();
            armasFiltradas = servicioArma.findByVidaMinima(vidaMinima);
        } else {
            // Filtrar solo por daño mínimo
            int dañoMinimo = jsonNode.get("dano_minimo").asInt();
            armasFiltradas = servicioArma.findByDañoMinimo(dañoMinimo);
        }

        if (armasFiltradas.isEmpty()) {
            return new ResponseEntity<>("No existen armas con esas características", HttpStatus.NOT_FOUND);
        }

        // Convertir a JSON
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

    private String verificarCamposYTipos(JsonNode jsonNode) {
        // Verificar existencia y tipo de cada campo
        if (!jsonNode.has("nombre") || !jsonNode.get("nombre").isTextual()) {
            return "El nombre tiene que ser un texto";
        }

        if (!jsonNode.has("daño") || !jsonNode.get("daño").isNumber()) {
            return "El daño tiene que ser un entero";
        }

        if (!jsonNode.has("municion") || !jsonNode.get("municion").isNumber()) {
            return "la municion tiene que ser un entero";
        }

        if (!jsonNode.has("vida") || !jsonNode.get("vida").isNumber()) {
            return "La vida tiene que ser un entero";
        }

        if (!jsonNode.has("velocidad") || !jsonNode.get("velocidad").isNumber()) {
            return "La velocidad tiene que ser un numero";
        }

        if (!jsonNode.has("fechaCreacion") || !jsonNode.get("fechaCreacion").isTextual()) {
            return "La fecha de creacion tiene que tener este formato [0000-00-00T00:00:00,Año-mes-diaTHora,Minutos,Sg]";
        }

        return "json valido";
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> añadirRifle(@RequestBody JsonNode jsonNode) {
        String res = verificarCamposYTipos(jsonNode);
        if(!res.equals("json valido")) {
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        if(jsonNode.get("nombre").asText().isEmpty()) {
            return ResponseEntity.badRequest().body("Arma sin nombre");
        }

        try {
            // Procesamos la munición
            Municion tipoMunicion;
            if (jsonNode.has("tipoMunicion") && !jsonNode.get("tipoMunicion").isNull()) {
                JsonNode municionNode = jsonNode.get("tipoMunicion");
                if (municionNode.has("nombre")) {
                    String nombreMunicion = municionNode.get("nombre").asText();
                    Optional<Municion> municionOpt = servicioMunicion.findByNombre(nombreMunicion);
                    tipoMunicion = municionOpt.orElse(servicioMunicion.getPredeterminada());
                } else {
                    tipoMunicion = servicioMunicion.getPredeterminada();
                }
            } else {
                tipoMunicion = servicioMunicion.getPredeterminada();
            }

            // Creamos el rifle con la munición correcta
            Rifle rifle = objectMapper.treeToValue(jsonNode, Rifle.class);
            rifle.setTipoMunicion(tipoMunicion);

            // Guardamos en la base de datos
            Arma rifleGuardado = servicioArma.añadirArma(rifle);

            return new ResponseEntity<>(rifleGuardado, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/")
    public ResponseEntity<?> eliminarArma(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("indice")) {
            return new ResponseEntity<>("Tienes que poner el campo indice con el que esta identificado el arma",
                    HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("indice").isInt()) {
            return new ResponseEntity<>("El indice tiene que ser un numero entero", HttpStatus.BAD_REQUEST);
        }

        int index = jsonNode.get("indice").asInt();

        if (!jsonNode.has("tipo")) {
            return new ResponseEntity<>("Falta especificar el tipo de arma", HttpStatus.BAD_REQUEST);
        }

        String tipo = jsonNode.get("tipo").asText();
        if (!tipo.equalsIgnoreCase("Rifle")) {
            return new ResponseEntity<>("El tipo de arma debe ser rifle o lanzador", HttpStatus.BAD_REQUEST);
        }

        // Buscamos el arma por índice y tipo
        List<Rifle> rifles = servicioArma.getRifles();
        for (Rifle rifle : rifles) {
            if (rifle.getIndex() == index) {
                servicioArma.eliminarArma(rifle);
                return new ResponseEntity<>(rifle, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/")
    public ResponseEntity<?> actualizarArma(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("indice")) {
            return new ResponseEntity<>("Ingresa el campo de indice", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.has("nombre") || jsonNode.get("nombre").asText().isEmpty()) {
            return new ResponseEntity<>("Nombre de arma no válido", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.has("tipo")) {
            return new ResponseEntity<>("Falta especificar el tipo de arma", HttpStatus.BAD_REQUEST);
        }

        int index = jsonNode.get("indice").asInt();
        String tipo = jsonNode.get("tipo").asText();

        if (!tipo.equalsIgnoreCase("Rifle")) {
            return new ResponseEntity<>("El tipo de arma debe ser rifle", HttpStatus.BAD_REQUEST);
        }

        // Buscar el arma a actualizar
        List<Rifle> rifles = servicioArma.getRifles();
        Rifle rifleExistente = null;

        for (Rifle rifle : rifles) {
            if (rifle.getIndex() == index) {
                rifleExistente = rifle;
                break;
            }
        }

        if (rifleExistente == null) {
            return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
        }

        try {
            // Creamos una copia del JSON para modificarla
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.remove("tipo");
            objectNode.remove("indice");

            // Verificamos que no exista otra arma con el mismo nombre
            String nuevoNombre = jsonNode.get("nombre").asText();
            Optional<Arma> existenteConMismoNombre = servicioArma.findByNombre(nuevoNombre);

            if (existenteConMismoNombre.isPresent() &&
                    !existenteConMismoNombre.get().getId().equals(rifleExistente.getId())) {
                return new ResponseEntity<>("Otra arma con el mismo nombre ya fue creada", HttpStatus.BAD_REQUEST);
            }

            // Procesamos la munición
            Municion tipoMunicion;
            if (objectNode.has("tipoMunicion") && !objectNode.get("tipoMunicion").isNull()) {
                JsonNode municionNode = objectNode.get("tipoMunicion");
                if (municionNode.has("nombre")) {
                    String nombreMunicion = municionNode.get("nombre").asText();
                    Optional<Municion> municionOpt = servicioMunicion.findByNombre(nombreMunicion);
                    tipoMunicion = municionOpt.orElse(rifleExistente.getTipoMunicion());
                } else {
                    tipoMunicion = rifleExistente.getTipoMunicion();
                }
            } else {
                tipoMunicion = rifleExistente.getTipoMunicion();
            }

            // Creamos el rifle actualizado
            Rifle nuevoRifle = objectMapper.treeToValue(objectNode, Rifle.class);
            nuevoRifle.setTipoMunicion(tipoMunicion);

            // Actualizamos en la base de datos
            Arma rifleActualizado = servicioArma.actualizarArma(rifleExistente, nuevoRifle);

            return new ResponseEntity<>(rifleActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}