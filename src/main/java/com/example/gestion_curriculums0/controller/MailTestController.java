package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/mail")
public class MailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTest(@RequestParam("to") String to) {
        String subject = "Prueba de correo - GestionaTusCV";
        String body = "Este es un correo de prueba enviado por la API.\n\nSaludos.";
        emailService.sendSimpleEmail(to, subject, body);
        return ResponseEntity.ok(Map.of("message", "ok", "to", to));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(emailService.mailStatus());
    }
}
