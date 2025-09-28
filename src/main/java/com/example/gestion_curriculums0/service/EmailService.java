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
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Inyecto el servicio de envío de correos

    @Value("${sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${app.mail.from:info@gestionatuscv.es}")
    private String fromEmail;

    @Value("${app.mail.admin:info@gestionatuscv.es}")
    private String adminEmail;

    @PostConstruct
    public void init() {
        // Fallback a variables de entorno si las propiedades no llegaron por alguna razón
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            String envKey = System.getenv("SENDGRID_API_KEY");
            if (envKey != null && !envKey.isBlank()) {
                sendGridApiKey = envKey;
            }
        }
        if (resendApiKey == null || resendApiKey.isBlank()) {
            String envKey = System.getenv("RESEND_API_KEY");
            if (envKey != null && !envKey.isBlank()) {
                resendApiKey = envKey;
            }
        }
        String envFrom = System.getenv("APP_MAIL_FROM");
        if (envFrom != null && !envFrom.isBlank()) {
            fromEmail = envFrom;
        }
        String envAdmin = System.getenv("APP_MAIL_ADMIN");
        if (envAdmin != null && !envAdmin.isBlank()) {
            adminEmail = envAdmin;
        }
        String mode = useResend() ? "Resend" : (useSendGrid() ? "SendGrid" : "SMTP");
        String keyPreview = "";
        if (useResend() && resendApiKey != null && resendApiKey.length() >= 6) {
            keyPreview = resendApiKey.substring(0,6) + "…";
        } else if (useSendGrid() && sendGridApiKey != null && sendGridApiKey.length() >= 6) {
            keyPreview = sendGridApiKey.substring(0,6) + "…";
        }
        System.out.println("[EmailService] mode=" + mode + ", from=" + fromEmail + ", admin=" + adminEmail + (keyPreview.isEmpty()?"":", key="+keyPreview));
    }

    private boolean useResend() { return resendApiKey != null && !resendApiKey.isBlank(); }
    private boolean useSendGrid() { return sendGridApiKey != null && !sendGridApiKey.isBlank(); }

    public java.util.Map<String, Object> mailStatus() {
        String mode = useResend() ? "Resend" : (useSendGrid() ? "SendGrid" : "SMTP");
        String sg = (sendGridApiKey == null) ? "null" : (sendGridApiKey.isBlank()?"blank":"set");
        String rs = (resendApiKey == null) ? "null" : (resendApiKey.isBlank()?"blank":"set");
        return java.util.Map.of(
                "mode", mode,
                "from", fromEmail,
                "admin", adminEmail,
                "sendgrid", sg,
                "resend", rs
        );
    }

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

    private void sendViaResend(String to, String subject, String text) throws Exception {
        String body = "{"
                + "\"from\":\"" + fromEmail + "\"," 
                + "\"to\":[\"" + to + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\"," 
                + "\"text\":\"" + escapeJson(text) + "\"" 
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = resp.statusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("Resend error status=" + status + ": " + resp.body());
        }
    }

    private void sendViaResend(String to, String subject, String text, String replyTo) throws Exception {
        String rt = (replyTo != null && !replyTo.isBlank()) ? "\"reply_to\":\"" + escapeJson(replyTo) + "\"," : "";
        String body = "{"
                + "\"from\":\"" + fromEmail + "\"," 
                + "\"to\":[\"" + to + "\"],"
                + rt
                + "\"subject\":\"" + escapeJson(subject) + "\"," 
                + "\"text\":\"" + escapeJson(text) + "\"" 
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = resp.statusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("Resend error status=" + status + ": " + resp.body());
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    /*
      Envío un correo de bienvenida a un nuevo usuario de forma asíncrona. Esto asegura que la
      operación de envío de correo no bloquee otras tareas.
     */
    @Async
    public void sendWelcomeEmail(String to, String username) {
        // Configuro el mensaje de bienvenida
        try {
            if (useResend()) {
                sendViaResend(to, "Bienvenido/a a nuestra plataforma",
                        "Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");
            } else if (useSendGrid()) {
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
            String prov = useResend()?"Resend":(useSendGrid()?"SendGrid":"SMTP");
            System.out.println("Correo de bienvenida enviado a: " + to + " (" + prov + ")");
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
            if (useResend()) {
                sendViaResend(to, subject, body);
            } else if (useSendGrid()) {
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
    // y una confirmación al remitente.
    public void sendContactEmail(ContactFormDTO contactForm) {
        String adminTo = adminEmail;
        String adminSubject = "Nuevo mensaje de contacto de " + contactForm.getName();
        String adminBody = "Nombre: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n\n" +
                "Mensaje: " + contactForm.getMessage();

        // 1) Enviar al administrador (crítico). Si falla, se propaga la excepción.
        try {
            if (useResend()) {
                sendViaResend(adminTo, adminSubject, adminBody, contactForm.getEmail());
            } else if (useSendGrid()) {
                sendViaSendGrid(adminTo, adminSubject, adminBody, contactForm.getEmail());
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setReplyTo(contactForm.getEmail()); // responder al usuario
                message.setTo(adminTo);
                message.setSubject(adminSubject);
                message.setText(adminBody);
                mailSender.send(message);
            }
            String prov = useResend()?"Resend":(useSendGrid()?"SendGrid":"SMTP");
            System.out.println("[EmailService] Contact enviado a admin: " + adminTo + " replyTo=" + contactForm.getEmail() + " (" + prov + ")");
        } catch (Exception e) {
            // Propagar para que el controlador devuelva 500 y el frontend muestre error
            throw new RuntimeException("Fallo enviando email de contacto a admin: " + e.getMessage(), e);
        }

        // 2) Enviar confirmación al usuario (no crítico). No rompemos el flujo si falla.
        try {
            String userTo = contactForm.getEmail();
            if (userTo != null && !userTo.isBlank()) {
                String userSubject = "Hemos recibido tu mensaje - GestionaTusCV";
                String userBody = "Hola " + contactForm.getName() + ",\n\n" +
                        "Gracias por escribirnos. Hemos recibido tu mensaje y te responderemos lo antes posible.\n\n" +
                        "Resumen del mensaje:\n" +
                        contactForm.getMessage() + "\n\n" +
                        "— Equipo de GestionaTusCV";
                if (useResend()) {
                    sendViaResend(userTo, userSubject, userBody);
                } else if (useSendGrid()) {
                    sendViaSendGrid(userTo, userSubject, userBody);
                } else {
                    SimpleMailMessage confirm = new SimpleMailMessage();
                    confirm.setFrom(fromEmail);
                    confirm.setTo(userTo);
                    confirm.setSubject(userSubject);
                    confirm.setText(userBody);
                    mailSender.send(confirm);
                }
                System.out.println("[EmailService] Confirmación de contacto enviada a usuario: " + userTo);
            }
        } catch (Exception ex) {
            // Log y continuar: el correo principal ya fue enviado al admin
            System.out.println("[EmailService] Aviso: no se pudo enviar confirmación al usuario: " + ex.getMessage());
        }
    }

    // Método genérico para enviar un correo simple a cualquier destinatario con un asunto y un cuerpo de texto
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            if (useResend()) {
                sendViaResend(to, subject, text);
            } else if (useSendGrid()) {
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
