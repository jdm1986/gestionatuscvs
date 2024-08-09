package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.ContactFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido a nuestra plataforma");
        message.setText("Hola " + username + ",\n\n¡Bienvenido a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");

        try {
            mailSender.send(message);
            System.out.println("Correo de bienvenida enviado a: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de bienvenida a: " + to);
        }
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
}
