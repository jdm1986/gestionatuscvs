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

    @PostMapping("/registrar")
    @Transactional
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre de usuario no puede estar vacío");
            }
            if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
                return ResponseEntity.badRequest().body("La contraseña no puede estar vacía");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("El email no puede estar vacío");
            }

            // Validación de contraseña con las reglas en UsuarioService
            usuarioService.validatePassword(usuario.getContrasena());

            Usuario savedUsuario = usuarioService.saveUsuario(usuario);
            System.out.println("Usuario registrado: " + savedUsuario.getNombreUsuario());

            return ResponseEntity.ok(convertToDTO(savedUsuario));
        } catch (IllegalArgumentException e) {
            // Si ocurre una excepción durante la validación, se devuelve un mensaje de error apropiado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(), Collections.singletonList(usuario.getRoles()),
                usuario.getCurriculums().stream().map(this::convertCurriculumToDTO).collect(Collectors.toList()));
    }

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
                curriculum.getFechaInsercion()
        );
    }

    @GetMapping("/listar")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/listar/{username}")
    public UsuarioDTO getUsuario(@PathVariable String username) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(usuario);
    }

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
