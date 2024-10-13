package com.example.gestion_curriculums0.config;

// Esta clase define configuraciones adicionales para mi aplicación web, especialmente CORS y el manejo de recursos estáticos.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Indico que esta clase es una configuración de Spring Boot
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Aquí configuro los ajustes de CORS (Cross-Origin Resource Sharing) para permitir peticiones desde mi frontend
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitimos solicitudes desde mi entorno de desarrollo y desde el dominio de producción
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "https://gestionatuscv.es")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Permitimos todos los métodos necesarios
                .allowedHeaders("*")  // Permitimos todos los encabezados necesarios
                .exposedHeaders("Authorization")  // Aseguro que el encabezado Authorization esté expuesto para manejar los tokens JWT
                .allowCredentials(true);  // Permito el uso de credenciales (cookies, cabeceras)
    }

    // Configuro el manejo de los recursos estáticos en mi aplicación, en este caso los recursos de frontend
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Defino que todos los recursos que están en la ruta /frontend/ deben buscarse en la carpeta classpath:/frontend/
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("classpath:/frontend/");
    }
}
