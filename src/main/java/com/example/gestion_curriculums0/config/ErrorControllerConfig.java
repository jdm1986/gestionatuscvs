// ErrorControllerConfig.java
package com.example.gestion_curriculums0.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorControllerConfig implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Devuelve el nombre de la plantilla de Thymeleaf "error-404" ubicada en "src/main/resources/templates/error-404.html"
        return "error-404";
    }
}
