// GestionCurriculums0Application.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0;

/*
   Esta es la clase principal que arranca la aplicación Spring Boot.
   Aquí configuro los paquetes a escanear, los repositorios JPA, y habilito funcionalidades como
   la programación de tareas asíncronas y la carga de variables de entorno desde un archivo .env.
*/

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.gestion_curriculums0")
@EnableJpaRepositories(basePackages = "com.example.gestion_curriculums0")
@EntityScan(basePackages = "com.example.gestion_curriculums0")
@EnableAsync // Habilito las operaciones asíncronas en la aplicación
@EnableScheduling // Habilito la programación de tareas automáticas
public class GestionCurriculums0Application {

    public static void main(String[] args) {
        // Cargo las variables de entorno desde el archivo .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Configuro las variables de entorno del sistema solo si están definidas en el .env
        setSystemProperty("DB_URL", dotenv.get("DB_URL"));
        setSystemProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        setSystemProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        setSystemProperty("SPRING_MAIL_PASSWORD", dotenv.get("SPRING_MAIL_PASSWORD"));
        setSystemProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        // Inicio la aplicación Spring Boot
        SpringApplication.run(GestionCurriculums0Application.class, args);
    }

    // Establezco la propiedad del sistema solo si el valor no es nulo
    private static void setSystemProperty(String key, String value) {
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.err.println("Warning: Environment variable " + key + " is not set.");
        }
    }
}

// Comandos de Maven para el entorno de desarrollo:
// mvn clean install -Pdev (cargar variables en local)
// mvn spring-boot:run -Pdev >>>>> ABRIR XAMP y cargar apache y mysql

// URL de acceso a la aplicación: http://localhost:8080
