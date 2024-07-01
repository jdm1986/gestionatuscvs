package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder; // Agrega esta línea

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest authRequest) {
        Usuario newUser = new Usuario();
        newUser.setNombreUsuario(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword())); // Usa passwordEncoder aquí
        newUser.setEmail(authRequest.getEmail());
        newUser.setRoles(authRequest.getRoles());
        usuarioRepository.save(newUser);
        // Guardar el nuevo usuario en la base de datos
        // Aquí debería haber una llamada a usuarioRepository.save(newUser);
        return "Usuario registrado exitosamente";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return jwt;
    }
}
