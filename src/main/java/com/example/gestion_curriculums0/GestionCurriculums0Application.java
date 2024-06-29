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

/* Iniciar el servidor web:

Abre una terminal o línea de comandos.
Navega al directorio que contiene tu archivo index.html. Puedes usar el comando cd para cambiar de directorio. Por ejemplo:


cd C:\cursos\java\IdeaProyect\gestion_curriculums0\frontend

Inicia el servidor web usando el siguiente comando:

python -m http.server 5500

Acceder a tu archivo:

Abrir navegador web y copiar http://localhost:5500. Deberías ver tu aplicación y poder realizar las solicitudes.*/
