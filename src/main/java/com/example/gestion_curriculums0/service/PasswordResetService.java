package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.baseUrl}")
    private String baseUrl;

    public synchronized void sendPasswordResetToken(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isEmpty()) return; // Evita enumeración

        Usuario usuario = optionalUsuario.get();
        LocalDateTime now = LocalDateTime.now();

        if (usuario.getResetToken() != null && usuario.getFechaTokenGenerado() != null) {
            Duration duration = Duration.between(usuario.getFechaTokenGenerado(), now);
            if (duration.toMinutes() < 15) return;
        }

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setFechaTokenGenerado(now);
        usuarioRepository.save(usuario);

        String resetUrl = baseUrl + "/reset-password.html?token=" + token;
        try {
            emailService.sendSimpleEmail(
                    email,
                    "Solicitud de restablecimiento de contraseña",
                    "Para restablecer tu contraseña, haz click en el siguiente enlace:\n" + resetUrl
            );
        } catch (Exception ignored) { }
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByResetToken(token);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setContrasena(passwordEncoder.encode(newPassword));
            usuario.setResetToken(null);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }
}

