package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8000")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Eliminamos el método forgotPassword de este controlador
}
