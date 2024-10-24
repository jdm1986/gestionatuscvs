// UsuarioService.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.model.UserLog;
import com.example.gestion_curriculums0.repository.*;
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

/* El UsuarioService proporciona la lógica de negocio relacionada con la gestión de usuarios.
Se encarga de validar contraseñas, enviar correos electrónicos (bienvenida, despedida), y registrar
logs de actividad. También maneja la eliminación automática de usuarios inactivos y la verificación
de sus roles. Esta clase actúa como una capa de servicio que facilita la interacción entre los controladores
y los repositorios, garantizando la integridad de los datos y una correcta gestión de las operaciones críticas
relacionadas con los usuarios. */

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

    // Guardo el usuario tras validar su contraseña y codificarla, además de registrar su log de actividad
    public Usuario saveUsuario(Usuario usuario) {
        validatePassword(usuario.getContrasena()); // Valido la contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena())); // Codifico la contraseña
        Usuario savedUsuario = usuarioRepository.save(usuario); // Guardo el usuario en la base de datos
        logUserAction(savedUsuario.getNombreUsuario(), "Registro de usuario"); // Registro en logs el registro de usuario
        sendWelcomeEmail(savedUsuario.getEmail(), savedUsuario.getNombreUsuario()); // Envío un correo de bienvenida
        return savedUsuario;
    }

    // Busco un usuario por su nombre de usuario
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    // Método para buscar un usuario por su ID
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    // Este método programado se ejecuta cada 10 minutos para eliminar usuarios inactivos.
    @Scheduled(fixedRate = 600000) // 600000 ms = 10 minutos
    @Transactional
    public void checkAndDeleteUsers() {
        System.out.println("Iniciando verificación de usuarios para eliminar...");
        Instant now = Instant.now();
        List<Usuario> usuarios = usuarioRepository.findAll(); // Obtengo todos los usuarios
        for (Usuario usuario : usuarios) {
            // Solo elimino usuarios con el rol "USER" y que se hayan registrado hace más de 30 minutos
            if (usuario.getRoles().contains("USER") &&
                    usuario.getFechaRegistro() != null &&
                    usuario.getFechaRegistro().toInstant().isBefore(now.minus(30, ChronoUnit.MINUTES))) {

                System.out.println("Enviando correo de despedida a usuario: " + usuario.getNombreUsuario());
                sendGoodbyeEmail(usuario.getEmail(), usuario.getNombreUsuario()); // Envío correo de despedida
                System.out.println("Eliminando usuario: " + usuario.getNombreUsuario());
                deleteUser(usuario); // Elimino el usuario
            }
        }
        System.out.println("Verificación de usuarios completada.");
    }

    // Elimino el usuario y todas sus entidades relacionadas
    @Transactional
    public void deleteUser(Usuario usuario) {
        try {
            // Convertir la fecha de registro a LocalDateTime
            LocalDateTime fechaRegistro = usuario.getFechaRegistro().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Registro en logs la eliminación del usuario con la fecha de registro original
            logUserAction(usuario.getNombreUsuario(), "Eliminación de usuario registrado el " + fechaRegistro);

            // Elimino todos los UploadLinks asociados al usuario
            uploadLinkRepository.deleteByUsuarioId(usuario.getId());

            // Elimino todas las notas relacionadas con cada currículum del usuario
            usuario.getCurriculums().forEach(curriculum -> {
                notaRepository.deleteByCurriculumId(curriculum.getId());
            });

            // Luego elimino los currículums asociados
            curriculumRepository.deleteByUsuarioId(usuario.getId());

            // Finalmente, elimino el usuario
            usuarioRepository.deleteById(usuario.getId());

            System.out.println("Usuario eliminado: " + usuario.getNombreUsuario());
        } catch (Exception e) {
            logUserAction(usuario.getNombreUsuario(), "Error al eliminar usuario"); // Registro el error en logs
            System.out.println("Error al eliminar usuario: " + usuario.getNombreUsuario());
            e.printStackTrace();
        }
    }

    // Registro una acción de usuario en el log
    private void logUserAction(String username, String action) {
        UserLog log = new UserLog();
        log.setUsername(username);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        userLogRepository.save(log);
    }

    // Envío un correo de bienvenida al usuario registrado
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

    // Envío un correo de despedida cuando el usuario es eliminado
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

    // Valido que la contraseña cumpla con los requisitos mínimos
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

    // Verifico si la contraseña contiene al menos un carácter especial
    private boolean containsSpecialCharacter(String password) {
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHARACTERS.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
}
