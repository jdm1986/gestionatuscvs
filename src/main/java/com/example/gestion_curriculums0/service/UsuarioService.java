package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Usuario saveUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        System.out.println("Usuario guardado: " + savedUsuario.getNombreUsuario());
        sendWelcomeEmail(savedUsuario.getEmail(), savedUsuario.getNombreUsuario());
        return savedUsuario;
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    @Scheduled(fixedRate = 60000) // 60000ms Ejecuta cada minuto 1800000 cada 30 minutos
    @Transactional
    public void checkAndDeleteUsers() {
        System.out.println("Verificando usuarios para eliminar...");
        Instant now = Instant.now();
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (usuario.getFechaRegistro() != null && usuario.getFechaRegistro().toInstant().isBefore(now.minus(30, ChronoUnit.MINUTES))) { //admite 30 minutos de prueba
                System.out.println("Eliminando usuario: " + usuario.getNombreUsuario());
                deleteUser(usuario);
            }
        }
    }

    @Transactional
    public void deleteUser(Usuario usuario) {
        try {
            curriculumRepository.deleteByUsuarioId(usuario.getId());
            usuarioRepository.deleteById(usuario.getId());
            System.out.println("Usuario eliminado: " + usuario.getNombreUsuario());
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + usuario.getNombreUsuario());
            e.printStackTrace();
        }
    }

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
}
