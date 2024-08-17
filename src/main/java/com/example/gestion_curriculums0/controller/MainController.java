package com.example.gestion_curriculums0.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "redirect:/frontend/index.html";  // Redirige al archivo de la carpeta frontend
    }
}
