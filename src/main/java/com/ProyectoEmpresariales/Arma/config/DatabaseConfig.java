package com.ProyectoEmpresariales.Arma.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    /**
     * Verifica la conexión a la base de datos Oracle cuando la aplicación inicia
     */
    @Bean
    public CommandLineRunner checkDatabaseConnection(@Autowired JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                logger.info("Comprobando conexión a Oracle...");
                String result = jdbcTemplate.queryForObject("SELECT 'Conexión exitosa a Oracle' FROM DUAL", String.class);
                logger.info(result);
            } catch (Exception e) {
                logger.error("Error al conectar con la base de datos Oracle: " + e.getMessage(), e);
                // No lanzar excepción para permitir que la aplicación continúe iniciándose
            }
        };
    }
}