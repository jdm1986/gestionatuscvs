package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.ContactFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido/a a nuestra plataforma");
        message.setText("Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");

        try {
            mailSender.send(message);
            System.out.println("Correo de bienvenida enviado a: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de bienvenida a: " + to);
        }
    }

    public void sendNotificationToAdmin(String username, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("gestionatuscv@gmail.com");
        message.setSubject("Nuevo Registro de Usuario en GestionaTusCV");
        message.setText("Un nuevo usuario se ha registrado en GestionaTusCV.\n\nDetalles del usuario:\n\n" +
                "Usuario: " + username + "\n" +
                "Email: " + email + "\n" +
                "Fecha de registro: " + LocalDateTime.now().toString() + "\n\n" +
                "Saludos,\nGestionaTusCV");
        mailSender.send(message);
    }

    public void sendContactEmail(ContactFormDTO contactForm) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("gestionatuscv@gmail.com");
        message.setSubject("Nuevo mensaje de contacto de " + contactForm.getName());
        message.setText("Nombre: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n\n" +
                "Mensaje: " + contactForm.getMessage());
        message.setFrom(contactForm.getEmail());

        mailSender.send(message);
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
