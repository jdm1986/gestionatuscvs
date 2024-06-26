package com.example.gestion_curriculums0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.gestion_curriculums0")
public class GestionCurriculums0Application {

	public static void main(String[] args) {
		SpringApplication.run(GestionCurriculums0Application.class, args);
	}
}
