package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    @Transactional
    public UsuarioDTO createUsuario(@RequestBody Usuario usuario) {
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(), usuario.getRoles(),
                usuario.getCurriculums().stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(
                curriculum.getId(),
                curriculum.getNombre(),
                curriculum.getApellido(),
                curriculum.getPdfPath(),
                curriculum.getResumenCv(),
                curriculum.getSexo(),
                curriculum.getTelefono(),
                curriculum.getEmail(),
                curriculum.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toList())
        );
    }

}
