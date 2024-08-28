package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @PostConstruct
    public void init() {
        // Detectar el entorno y ajustar la URL base en consecuencia
        String environment = System.getenv("ENVIRONMENT");
        if ("production".equalsIgnoreCase(environment)) {
            baseUrl = "https://gestionatuscv.es";
        } else {
            baseUrl = "http://localhost:8000";
        }
    }

    public void sendPasswordResetToken(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuarioRepository.save(usuario);

            String resetUrl = baseUrl + "/reset-password.html?token=" + token;
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Solicitud de restablecimiento de contraseña");
            mailMessage.setText("Para restablecer tu contraseña, haz click en el siguiente enlace:\n" + resetUrl);
            mailSender.send(mailMessage);
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByResetToken(token);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setContrasena(passwordEncoder.encode(newPassword));
            usuario.setResetToken(null);
            usuarioRepository.save(usuario);
            return true;
        } else {
            return false;
        }
    }
}
