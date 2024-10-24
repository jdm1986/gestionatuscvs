// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// Con la anotación @Controller, indico que esta clase se comporta como un controlador en Spring MVC

@Controller
public class ErrorControllerConfig implements ErrorController {

    // Manejo las solicitudes que se realizan a la URL "/error"
    @RequestMapping("/error")
    public String handleError() {
        // Devuelvo el nombre de la plantilla de Thymeleaf "error-404"
        // que se encuentra en "src/main/resources/templates/error-404.html"
        // para mostrar una página personalizada de error 404
        return "error-404";
    }
}
