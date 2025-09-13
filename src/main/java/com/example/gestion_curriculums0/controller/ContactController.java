// ContactController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Este controlador gestiona el envío de formularios de contacto en mi aplicación

import com.example.gestion_curriculums0.model.ContactFormDTO;
import com.example.gestion_curriculums0.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Defino que este controlador está asociado a la ruta "/contact" y acepto solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/contact")
public class ContactController {

    // Inyecto el servicio de correo electrónico para manejar el envío de los mensajes del formulario de contacto
    @Autowired
    private EmailService emailService;

    // Configuro un endpoint para enviar un formulario de contacto, recibiendo los datos como un DTO
    @PostMapping
    public ResponseEntity<String> sendContactForm(@RequestBody ContactFormDTO contactForm) {
        try {
            // Envío el formulario de contacto usando el servicio de correo electrónico
            emailService.sendContactEmail(contactForm);
            return ResponseEntity.ok("Mensaje enviado exitosamente");
        } catch (Exception e) {
            // Si ocurre algún error durante el envío, devuelvo un mensaje de error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al enviar el mensaje: " + e.getMessage());
        }
    }
}
