// PasswordResetController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Defino este controlador para manejar las operaciones relacionadas con el restablecimiento de contraseñas

import com.example.gestion_curriculums0.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Anoto la clase como un controlador REST y la asocio a la ruta base "/auth", permitiendo solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    // Inyecto el servicio de restablecimiento de contraseñas para manejar la lógica relacionada
    @Autowired
    private PasswordResetService passwordResetService;

    // He eliminado el método forgotPassword en este controlador, ya que decidí no implementarlo aquí
}
