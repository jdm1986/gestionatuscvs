package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Defino este controlador para manejar las operaciones relacionadas con el restablecimiento de contraseñas
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8000")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // He eliminado el método forgotPassword en este controlador, ya que decidí no implementarlo aquí.
}
