package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import com.example.gestion_curriculums0.repository.NotaRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private NotaRepository notaRepository;  // Agregar el repositorio de notas

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

    @Scheduled(fixedRate = 600000) // Ejecuta cada 10 minutos (600000 ms)
    @Transactional
    public void checkAndDeleteUsers() {
        System.out.println("Iniciando verificación de usuarios para eliminar...");
        Instant now = Instant.now();
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            // Evitar eliminar usuarios con roles distintos de "ROLE_USER"
            if (usuario.getRoles().contains("USER") &&
                    usuario.getFechaRegistro() != null &&
                    usuario.getFechaRegistro().toInstant().isBefore(now.minus(30, ChronoUnit.MINUTES))) {

                System.out.println("Enviando correo de despedida a usuario: " + usuario.getNombreUsuario());
                sendGoodbyeEmail(usuario.getEmail(), usuario.getNombreUsuario());
                System.out.println("Eliminando usuario: " + usuario.getNombreUsuario());
                deleteUser(usuario);
            }
        }
        System.out.println("Verificación de usuarios completada.");
    }

    @Transactional
    public void deleteUser(Usuario usuario) {
        try {
            // Eliminar todas las notas relacionadas con cada currículum del usuario
            usuario.getCurriculums().forEach(curriculum -> {
                notaRepository.deleteByCurriculumId(curriculum.getId());
            });

            // Luego eliminar los currículums asociados
            curriculumRepository.deleteByUsuarioId(usuario.getId());

            // Finalmente, eliminar el usuario
            usuarioRepository.deleteById(usuario.getId());

            System.out.println("Usuario eliminado: " + usuario.getNombreUsuario());
        } catch (Exception e) {
            System.out.println("Error al eliminar usuario: " + usuario.getNombreUsuario());
            e.printStackTrace();
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenid@ a nuestra plataforma");
        message.setText("Hola " + username + ",\n\n¡Bienvenid@ a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");

        try {
            mailSender.send(message);
            System.out.println("Correo de bienvenida enviado a: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de bienvenida a: " + to);
        }
    }

    public void sendGoodbyeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Gracias por probar nuestra aplicación");
        message.setText("Hola " + username + ",\n\nGracias por visitar y probar nuestra aplicación. Esperamos que hayas tenido una buena experiencia.\n\nSaludos,\nEl equipo");

        try {
            mailSender.send(message);
            System.out.println("Correo de despedida enviado a: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de despedida a: " + to);
        }
    }
}
