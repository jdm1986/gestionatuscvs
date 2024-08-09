package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.ContactFormDTO;
import com.example.gestion_curriculums0.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@CrossOrigin(origins = "http://localhost:8000")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendContactForm(@RequestBody ContactFormDTO contactForm) {
        try {
            emailService.sendContactEmail(contactForm);
            return ResponseEntity.ok("Mensaje enviado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al enviar el mensaje: " + e.getMessage());
        }
    }
}
