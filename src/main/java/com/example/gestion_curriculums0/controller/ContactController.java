// Este controlador gestiona el envío de formularios de contacto en mi aplicación.

package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.ContactFormDTO;
import com.example.gestion_curriculums0.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Indico que este controlador está asociado a la ruta "/contact"
@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = "http://localhost:8000")
public class ContactController {

    // Inyecto el servicio de correo electrónico para manejar el envío de mensajes
    @Autowired
    private EmailService emailService;

    // Endpoint para enviar un formulario de contacto, donde recibo el cuerpo del formulario como un DTO
    @PostMapping
    public ResponseEntity<String> sendContactForm(@RequestBody ContactFormDTO contactForm) {
        try {
            // Uso el servicio de correo para enviar el formulario de contacto
            emailService.sendContactEmail(contactForm);
            return ResponseEntity.ok("Mensaje enviado exitosamente");
        } catch (Exception e) {
            // Manejo cualquier error durante el proceso de envío
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al enviar el mensaje: " + e.getMessage());
        }
    }
}
