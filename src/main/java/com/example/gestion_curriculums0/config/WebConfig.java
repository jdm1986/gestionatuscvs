// WebConfig.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.config;

// Esta clase define configuraciones adicionales para mi aplicación web, especialmente CORS y el manejo de recursos estáticos

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Con la anotación @Configuration, indico que esta clase es una configuración de Spring Boot
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Configuro los ajustes de CORS (Cross-Origin Resource Sharing) para permitir peticiones desde mi frontend
    // CORS se configura centralizadamente en Security a través de CorsConfigurationSource

    // Configuro el manejo de los recursos estáticos en mi aplicación, en este caso los recursos de frontend
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Defino que todos los recursos en la ruta /frontend/ se deben buscar en la carpeta classpath:/frontend/
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("classpath:/frontend/");
    }
}
