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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // Crear una lista de DTOs para incluir información de munición
        List<Map<String, Object>> armasConDetalles = new ArrayList<>();

        for (Arma arma : armas) {
            Map<String, Object> armaDTO = new HashMap<>();
            armaDTO.put("id", arma.getId());
            armaDTO.put("nombre", arma.getNombre());
            armaDTO.put("daño", arma.getDaño());
            armaDTO.put("municion", arma.getMunicion());
            armaDTO.put("vida", arma.getVida());
            armaDTO.put("distancia", arma.getDistancia());
            armaDTO.put("fechaCreacion", arma.getFechaCreacion());
            armaDTO.put("capMunicion", arma.getCapMunicion());
            armaDTO.put("tipoArma", arma.getTipoArma());

            // Incluir información adicional si es un rifle
            if (arma instanceof Rifle) {
                Rifle rifle = (Rifle) arma;
                armaDTO.put("velocidad", rifle.getVelocidad());

                // Añadir información básica de la munición
                Municion municion = rifle.getTipoMunicion();
                Map<String, Object> municionInfo = new HashMap<>();
                municionInfo.put("id", municion.getId());
                municionInfo.put("nombre", municion.getNombre());
                municionInfo.put("cadencia", municion.getCadencia());
                municionInfo.put("dañoArea", municion.isDañoArea());

                armaDTO.put("tipoMunicion", municionInfo);
            }

            armasConDetalles.add(armaDTO);
        }

        return new ResponseEntity<>(armasConDetalles, HttpStatus.OK);
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
        if (!jsonNode.has("id")) {
            return new ResponseEntity<>("El json debe tener un atributo id", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("id").isNumber()) {
            return new ResponseEntity<>("El valor del id debe ser numérico", HttpStatus.BAD_REQUEST);
        }

        Long id = jsonNode.get("id").asLong();

        // Comprobar si existe un arma con ese ID
        Optional<Arma> armaOpt = servicioArma.findById(id);

        if (armaOpt.isPresent()) {
            Arma arma = armaOpt.get();
            // Verificar si es un rifle
            if (arma instanceof Rifle) {
                return new ResponseEntity<>(arma, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("El arma encontrada no es un rifle", HttpStatus.BAD_REQUEST);
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

        // Verificar si el arma existe
        if (armaOpt.isEmpty()) {
            return new ResponseEntity<>("No se encontró ningún arma con el nombre: " + nombre, HttpStatus.NOT_FOUND);
        }

        Rifle rifle = (Rifle) armaOpt.get();

        Map<String, Object> armaDTO = new HashMap<>();
        armaDTO.put("id", rifle.getId());
        armaDTO.put("nombre", rifle.getNombre());
        armaDTO.put("daño", rifle.getDaño());
        armaDTO.put("municion", rifle.getMunicion());
        armaDTO.put("vida", rifle.getVida());
        armaDTO.put("distancia", rifle.getDistancia());
        armaDTO.put("fechaCreacion", rifle.getFechaCreacion());
        armaDTO.put("capMunicion", rifle.getCapMunicion());
        armaDTO.put("tipoArma", rifle.getTipoArma());
        armaDTO.put("velocidad", rifle.getVelocidad());

        // Añadir información básica de la munición
        Municion municion = rifle.getTipoMunicion();
        Map<String, Object> municionInfo = new HashMap<>();
        municionInfo.put("id", municion.getId());
        municionInfo.put("nombre", municion.getNombre());
        municionInfo.put("cadencia", municion.getCadencia());
        municionInfo.put("dañoArea", municion.isDañoArea());
        armaDTO.put("tipoMunicion", municionInfo);

        return new ResponseEntity<>(armaDTO, HttpStatus.OK);
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

        // Crear lista de DTOs con información detallada incluyendo la munición
        List<Map<String, Object>> armasDTO = new ArrayList<>();

        for (Arma arma : armasFiltradas) {
            Map<String, Object> armaDTO = new HashMap<>();

            // Información básica del arma
            armaDTO.put("id", arma.getId());
            armaDTO.put("nombre", arma.getNombre());
            armaDTO.put("daño", arma.getDaño());
            armaDTO.put("municion", arma.getMunicion());
            armaDTO.put("vida", arma.getVida());
            armaDTO.put("distancia", arma.getDistancia());
            armaDTO.put("fechaCreacion", arma.getFechaCreacion());

            // Si es un rifle, incluir campos específicos de rifle
            if (arma instanceof Rifle) {
                Rifle rifle = (Rifle) arma;
                armaDTO.put("capMunicion", rifle.getCapMunicion());
                armaDTO.put("tipoArma", rifle.getTipoArma());
                armaDTO.put("velocidad", rifle.getVelocidad());

                // Añadir información completa de la munición
                Municion municion = rifle.getTipoMunicion();
                if (municion != null) {
                    Map<String, Object> municionInfo = new HashMap<>();
                    municionInfo.put("id", municion.getId());
                    municionInfo.put("nombre", municion.getNombre());
                    municionInfo.put("cadencia", municion.getCadencia());
                    municionInfo.put("dañoArea", municion.isDañoArea());
                    // Agregar más propiedades de munición si existen

                    armaDTO.put("tipoMunicion", municionInfo);
                }
            }

            // Agregar cada DTO a la lista
            armasDTO.add(armaDTO);
        }

        // Retornar directamente el objeto, permitiendo que Spring lo convierta a JSON
        return new ResponseEntity<>(armasDTO, HttpStatus.OK);
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
        if (!jsonNode.has("id")) {
            return new ResponseEntity<>("Tienes que proporcionar el ID del arma", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.get("id").isNumber()) {
            return new ResponseEntity<>("El ID debe ser un número", HttpStatus.BAD_REQUEST);
        }

        Long id = jsonNode.get("id").asLong();

        // Buscar el arma por ID
        Optional<Arma> armaOpt = servicioArma.findById(id);

        if (!armaOpt.isPresent()) {
            return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
        }

        // Verificar si es un rifle
        Arma arma = armaOpt.get();
        if (!(arma instanceof Rifle)) {
            return new ResponseEntity<>("El arma encontrada no es del tipo esperado", HttpStatus.BAD_REQUEST);
        }

        Rifle rifle = (Rifle) arma;

        // Crear un DTO simplificado para la respuesta
        Map<String, Object> armaDTO = new HashMap<>();
        armaDTO.put("id", rifle.getId());
        armaDTO.put("nombre", rifle.getNombre());
        armaDTO.put("daño", rifle.getDaño());
        armaDTO.put("municion", rifle.getMunicion());
        armaDTO.put("vida", rifle.getVida());
        armaDTO.put("velocidad", rifle.getVelocidad());

        // No incluimos la relación tipoMunicion para evitar problemas de lazy loading
        Map<String, Object> municionInfo = new HashMap<>();
        municionInfo.put("id", rifle.getTipoMunicion().getId());
        municionInfo.put("nombre", rifle.getTipoMunicion().getNombre());
        armaDTO.put("tipoMunicion", municionInfo);

        // Eliminar el arma
        servicioArma.eliminarArma(arma);

        // Devolver el DTO en lugar de la entidad
        return new ResponseEntity<>(armaDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/")
    public ResponseEntity<?> actualizarArma(@RequestBody JsonNode jsonNode) {
        if (!jsonNode.has("id")) {
            return new ResponseEntity<>("Ingresa el ID del arma", HttpStatus.BAD_REQUEST);
        }

        if (!jsonNode.has("nombre") || jsonNode.get("nombre").asText().isEmpty()) {
            return new ResponseEntity<>("Nombre de arma no válido", HttpStatus.BAD_REQUEST);
        }

        Long id = jsonNode.get("id").asLong();

        // Buscar el arma a actualizar por ID
        Optional<Arma> armaOpt = servicioArma.findById(id);

        if (!armaOpt.isPresent()) {
            return new ResponseEntity<>("Arma no encontrada", HttpStatus.NOT_FOUND);
        }

        // Verificar si es un rifle
        Arma arma = armaOpt.get();
        if (!(arma instanceof Rifle)) {
            return new ResponseEntity<>("El arma encontrada no es un rifle", HttpStatus.BAD_REQUEST);
        }

        Rifle rifleExistente = (Rifle) arma;

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

            // Crear un DTO para la respuesta con la información completa incluyendo la munición
            Map<String, Object> rifleDTO = new HashMap<>();
            Rifle rifleRespuesta = (Rifle) rifleActualizado;

            // Información básica del rifle
            rifleDTO.put("id", rifleRespuesta.getId());
            rifleDTO.put("nombre", rifleRespuesta.getNombre());
            rifleDTO.put("daño", rifleRespuesta.getDaño());
            rifleDTO.put("municion", rifleRespuesta.getMunicion());
            rifleDTO.put("vida", rifleRespuesta.getVida());
            rifleDTO.put("distancia", rifleRespuesta.getDistancia());
            rifleDTO.put("fechaCreacion", rifleRespuesta.getFechaCreacion());
            rifleDTO.put("capMunicion", rifleRespuesta.getCapMunicion());
            rifleDTO.put("tipoArma", rifleRespuesta.getTipoArma());
            rifleDTO.put("velocidad", rifleRespuesta.getVelocidad());

            // Añadir información completa de la munición
            Municion municion = rifleRespuesta.getTipoMunicion();
            if (municion != null) {
                Map<String, Object> municionInfo = new HashMap<>();
                municionInfo.put("id", municion.getId());
                municionInfo.put("nombre", municion.getNombre());
                municionInfo.put("cadencia", municion.getCadencia());
                municionInfo.put("dañoArea", municion.isDañoArea());
                // Agregar más propiedades de munición si existen

                rifleDTO.put("tipoMunicion", municionInfo);
            }

            return new ResponseEntity<>(rifleDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}