package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.ContactFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
  Clase EmailService: Responsable de enviar correos electrónicos, como correos de bienvenida,
  notificaciones a administradores y correos de contacto. Además, proporciono la funcionalidad
  para enviar correos de manera asíncrona y directa.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Inyecto el servicio de envío de correos

    /*
      Envío un correo de bienvenida a un nuevo usuario de forma asíncrona. Esto asegura que la
      operación de envío de correo no bloquee otras tareas.
     */

    @Async
    public void sendWelcomeEmail(String to, String username) {
        // Configuro el mensaje de bienvenida
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido/a a nuestra plataforma");
        message.setText("Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");

        try {
            // Intento enviar el correo
            mailSender.send(message);
            System.out.println("Correo de bienvenida enviado a: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de bienvenida a: " + to);
        }
    }

    //Envío una notificación al administrador cada vez que un nuevo usuario se registra en el sistema.

    public void sendNotificationToAdmin(String username, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("gestionatuscv@gmail.com");
        message.setSubject("Nuevo Registro de Usuario en GestionaTusCV");
        message.setText("Un nuevo usuario se ha registrado en GestionaTusCV.\n\nDetalles del usuario:\n\n" +
                "Usuario: " + username + "\n" +
                "Email: " + email + "\n" +
                "Fecha de registro: " + LocalDateTime.now().toString() + "\n\n" +
                "Saludos,\nGestionaTusCV");
        // Envío el correo al administrador
        mailSender.send(message);
    }

    //Envío un correo con los detalles del formulario de contacto a la dirección del administrador.

    public void sendContactEmail(ContactFormDTO contactForm) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("gestionatuscv@gmail.com");
        message.setSubject("Nuevo mensaje de contacto de " + contactForm.getName());
        message.setText("Nombre: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n\n" +
                "Mensaje: " + contactForm.getMessage());
        message.setFrom(contactForm.getEmail()); // Establezco el email del remitente

        // Envío el mensaje de contacto
        mailSender.send(message);
    }

    //Método genérico para enviar un correo simple a cualquier destinatario con un asunto y un cuerpo de texto.

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        // Envío el correo
        mailSender.send(message);
    }
}
