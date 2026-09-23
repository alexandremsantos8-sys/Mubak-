package com.Senai.Mubak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Ponto de entrada: inicializa o Spring Boot e todos os componentes do Mubak. */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// O Spring cria controllers, services, repositories e configura o servidor web.
		SpringApplication.run(Application.class, args);
	}

}
