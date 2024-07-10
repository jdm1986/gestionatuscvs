package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.model.UsuarioDTO;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    @Transactional
    public UsuarioDTO createUsuario(@RequestBody Usuario usuario) {
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        System.out.println("Usuario registrado: " + savedUsuario.getNombreUsuario());
        return convertToDTO(savedUsuario);
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
}
