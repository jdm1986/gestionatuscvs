package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.model.UserLog;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import com.example.gestion_curriculums0.repository.NotaRepository;
import com.example.gestion_curriculums0.repository.UploadLinkRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.repository.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private NotaRepository notaRepository;

    @Autowired
    private UploadLinkRepository uploadLinkRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    public Usuario saveUsuario(Usuario usuario) {
        validatePassword(usuario.getContrasena());
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        logUserAction(savedUsuario.getNombreUsuario(), "Registro de usuario");
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
            // Convertir la fecha de registro a LocalDateTime
            LocalDateTime fechaRegistro = usuario.getFechaRegistro().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Registrar acción de eliminación de usuario con la fecha de registro original
            logUserAction(usuario.getNombreUsuario(), "Eliminación de usuario registrado el " + fechaRegistro);

            // Eliminar todos los UploadLinks asociados al usuario
            uploadLinkRepository.deleteByUsuarioId(usuario.getId());

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
            logUserAction(usuario.getNombreUsuario(), "Error al eliminar usuario");
            System.out.println("Error al eliminar usuario: " + usuario.getNombreUsuario());
            e.printStackTrace();
        }
    }

    private void logUserAction(String username, String action) {
        UserLog log = new UserLog();
        log.setUsername(username);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        userLogRepository.save(log);
    }

    public void sendWelcomeEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido/a a nuestra plataforma");
        message.setText("Hola " + username + ",\n\n¡Bienvenido/a a nuestra plataforma! Estamos encantados de tenerte con nosotros.\n\nSaludos,\nEl equipo");

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

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        if (!containsSpecialCharacter(password)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial: " + SPECIAL_CHARACTERS);
        }
    }

    private boolean containsSpecialCharacter(String password) {
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHARACTERS.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
}

