package com.ProyectoEmpresariales.Arma;

import com.ProyectoEmpresariales.Arma.servicios.ServicioArma;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;


@SpringBootApplication

public class ArmaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArmaApplication.class, args);
	}
	@Bean
	public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> webServerCustomizer() {
		return factory -> factory.setPort(8080);
	}

}
