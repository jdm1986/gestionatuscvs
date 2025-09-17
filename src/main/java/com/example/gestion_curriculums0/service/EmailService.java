// EmailService.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.service;

/*
  Clase EmailService: Responsable de enviar correos electrónicos, como correos de bienvenida,
  notificaciones a administradores y correos de contacto. También proporciono la funcionalidad
  para enviar correos de manera asíncrona y directa.
 */

import com.example.gestion_curriculums0.model.ContactFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Inyecto el servicio de envío de correos

    @Value("${sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${app.mail.from:gestionatuscv@gmail.com}")
    private String fromEmail;

    @Value("${app.mail.admin:info@gestionatuscv.es}")
    private String adminEmail;

    private boolean useSendGrid() { return sendGridApiKey != null && !sendGridApiKey.isBlank(); }

    private void sendViaSendGrid(String to, String subject, String text) throws Exception {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("SendGrid error status=" + status + ": " + response.getBody());
        }
    }

    private void sendViaSendGrid(String to, String subject, String text, String replyTo) throws Exception {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);
        if (replyTo != null && !replyTo.isBlank()) {
            mail.setReplyTo(new Email(replyTo));
        }
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        int status = response.getStatusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("SendGrid error status=" + status + ": " + response.getBody());
        }
    }

    /*
      Envío un correo de bienvenida a un nuevo usuario de forma asíncrona. Esto asegura que la
      operación de envío de correo no bloquee otras tareas.
     */
    @Async
    public void sendWelcomeEmail(String to, String username) {
        // Configuro el mensaje de bienvenida
        try {
            if (useSendGrid()) {
                sendViaSendGrid(to, "Bienvenido/a a nuestra plataforma",
                        "Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject("Bienvenido/a a nuestra plataforma");
                message.setText("Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");
                mailSender.send(message);
            }
            System.out.println("Correo de bienvenida enviado a: " + to + (useSendGrid()?" (SendGrid)":" (SMTP)"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de bienvenida a: " + to);
        }
    }

    // Envío una notificación al administrador cada vez que un nuevo usuario se registra en el sistema
    public void sendNotificationToAdmin(String username, String email) {
        String to = adminEmail;
        String subject = "Nuevo Registro de Usuario en GestionaTusCV";
        String body = "Un nuevo usuario se ha registrado en GestionaTusCV.\n\nDetalles del usuario:\n\n" +
                "Usuario: " + username + "\n" +
                "Email: " + email + "\n" +
                "Fecha de registro: " + LocalDateTime.now().toString() + "\n\n" +
                "Saludos,\nGestionaTusCV";
        try {
            if (useSendGrid()) {
                sendViaSendGrid(to, subject, body);
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envío un correo con los detalles del formulario de contacto a la dirección del administrador
    public void sendContactEmail(ContactFormDTO contactForm) {
        String to = adminEmail;
        String subject = "Nuevo mensaje de contacto de " + contactForm.getName();
        String body = "Nombre: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n\n" +
                "Mensaje: " + contactForm.getMessage();
        try {
            if (useSendGrid()) {
                // Envío desde el remitente del dominio y pongo Reply-To al correo del usuario del formulario
                sendViaSendGrid(to, subject, body, contactForm.getEmail());
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                // From debe ser del dominio propio; usamos Reply-To para que al responder vaya al usuario
                message.setFrom(fromEmail);
                message.setReplyTo(contactForm.getEmail());
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método genérico para enviar un correo simple a cualquier destinatario con un asunto y un cuerpo de texto
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            if (useSendGrid()) {
                sendViaSendGrid(to, subject, text);
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                mailSender.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
