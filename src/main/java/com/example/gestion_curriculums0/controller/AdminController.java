package com.example.gestion_curriculums0.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "This is a protected admin endpoint";
    }
}
