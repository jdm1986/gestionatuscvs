// WebMvcConfig.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.config;

// Esta clase configura los controladores de vistas para mi aplicación
// Aquí defino cómo manejar ciertas rutas sin necesidad de un controlador explícito

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Con la anotación @Configuration, indico que esta es una clase de configuración para la parte MVC de mi aplicación
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Configuro un controlador de vista para redirigir la raíz de mi aplicación hacia el archivo index.html
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Raíz → home.html (forward evita cambio de URL)
        registry.addViewController("/").setViewName("forward:/home.html");
        // index.html → home.html (redirect explícito por si se referencia/cacha)
        registry.addRedirectViewController("/index.html", "/home.html");
    }
}
