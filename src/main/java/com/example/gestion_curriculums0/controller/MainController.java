package com.example.gestion_curriculums0.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";  // Asegúrate de que tienes un archivo "index.html" en tu carpeta de plantillas.
    }
}
