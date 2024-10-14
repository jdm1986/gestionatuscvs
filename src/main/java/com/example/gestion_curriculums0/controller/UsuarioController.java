package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.model.UsuarioDTO;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.CustomUserDetailsService;
import com.example.gestion_curriculums0.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Defino este controlador para gestionar las operaciones relacionadas con los usuarios
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para registrar un nuevo usuario
    @PostMapping("/registrar")
    @Transactional
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        try {
            // Verifico que el nombre de usuario no esté vacío
            if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre de usuario no puede estar vacío");
            }
            // Verifico que la contraseña no esté vacía
            if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
                return ResponseEntity.badRequest().body("La contraseña no puede estar vacía");
            }
            // Verifico que el email no esté vacío
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("El email no puede estar vacío");
            }

            // Valido la contraseña utilizando las reglas que definí en UsuarioService
            usuarioService.validatePassword(usuario.getContrasena());

            // Guardo el usuario y devuelvo el DTO correspondiente
            Usuario savedUsuario = usuarioService.saveUsuario(usuario);
            System.out.println("Usuario registrado: " + savedUsuario.getNombreUsuario());

            return ResponseEntity.ok(convertToDTO(savedUsuario));
        } catch (IllegalArgumentException e) {
            // Si hay algún error en la validación, devuelvo un mensaje de error apropiado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Método auxiliar para convertir un usuario en DTO
    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(), Collections.singletonList(usuario.getRoles()),
                usuario.getCurriculums().stream().map(this::convertCurriculumToDTO).collect(Collectors.toList()));
    }

    // Método auxiliar para convertir un curriculum en DTO
    private CurriculumDTO convertCurriculumToDTO(Curriculum curriculum) {
        return new CurriculumDTO(
                curriculum.getId(),
                curriculum.getNombre(),
                curriculum.getApellido(),
                curriculum.getPdfPath(),
                curriculum.getResumenCv(),
                curriculum.getSexo(),
                curriculum.getTelefono(),
                curriculum.getEmail(),
                curriculum.getDepartamento(),
                curriculum.getFechaInsercion()
        );
    }

    // Listado de todos los usuarios registrados
    @GetMapping("/listar")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtengo un usuario por su nombre de usuario
    @GetMapping("/listar/{username}")
    public UsuarioDTO getUsuario(@PathVariable String username) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(usuario);
    }

    // Elimino un usuario por su nombre de usuario
    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        Optional<Usuario> usuarioOpt = usuarioService.findByNombreUsuario(username);
        if (usuarioOpt.isPresent()) {
            usuarioService.deleteUser(usuarioOpt.get());
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}
