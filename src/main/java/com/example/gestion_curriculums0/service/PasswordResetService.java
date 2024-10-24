// PasswordResetService.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/* Esta clase gestiona el proceso completo para restablecer contraseñas de los usuarios.
Genera un token único y temporal que se envía al correo del usuario, permitiéndole restablecer
su contraseña de manera segura. Verifica la validez del token antes de permitir el cambio de contraseña,
evitando que un token usado previamente pueda ser reutilizado. Además, establece límites para evitar el
abuso del sistema de restablecimiento de contraseñas (por ejemplo, un solo token cada 15 minutos),
contribuyendo a la seguridad general de la aplicación. */

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Inyecto el repositorio de usuarios

    @Autowired
    private JavaMailSender mailSender; // Inyecto el servicio de envío de correos

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyecto el codificador de contraseñas

    // Uso esta propiedad para construir la URL base para el enlace de restablecimiento de contraseña
    @Value("${app.baseUrl}")
    private String baseUrl;

    // Este método envía un correo electrónico con el token de restablecimiento de contraseña al usuario
    public synchronized void sendPasswordResetToken(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            LocalDateTime now = LocalDateTime.now();

            // Verifico si ya hay un token generado recientemente para evitar abusos (solo uno cada 15 minutos)
            if (usuario.getResetToken() != null && usuario.getFechaTokenGenerado() != null) {
                Duration duration = Duration.between(usuario.getFechaTokenGenerado(), now);
                if (duration.toMinutes() < 15) {
                    return; // Evito enviar un nuevo token si no han pasado 15 minutos
                }
            }

            // Genero un nuevo token único para el restablecimiento de contraseña
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setFechaTokenGenerado(now);
            usuarioRepository.save(usuario); // Guardo el token y la fecha en la base de datos

            // Construyo la URL para el enlace de restablecimiento de contraseña
            String resetUrl = baseUrl + "/reset-password.html?token=" + token;
            System.out.println("Enviando correo de restablecimiento a: " + email + " con token: " + token);

            // Envío el correo con el enlace de restablecimiento
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Solicitud de restablecimiento de contraseña");
            mailMessage.setText("Para restablecer tu contraseña, haz click en el siguiente enlace:\n" + resetUrl);
            mailSender.send(mailMessage);
        }
    }

    // Este método valida el token y permite que el usuario restablezca su contraseña
    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByResetToken(token);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Codifico la nueva contraseña antes de guardarla en la base de datos
            usuario.setContrasena(passwordEncoder.encode(newPassword));
            usuario.setResetToken(null); // Elimino el token una vez se ha utilizado
            usuarioRepository.save(usuario); // Actualizo al usuario en la base de datos
            return true;
        } else {
            return false; // Si el token no es válido o ha expirado, el restablecimiento falla
        }
    }
}
