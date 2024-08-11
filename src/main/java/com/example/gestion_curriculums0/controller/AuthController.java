package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.AuthRequest;
import com.example.gestion_curriculums0.service.PasswordResetService;
import com.example.gestion_curriculums0.service.CustomUserDetailsService;
import com.example.gestion_curriculums0.service.EmailService;
import com.example.gestion_curriculums0.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);
            Long userId = userDetailsService.getUserIdByUsername(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest) {
        userDetailsService.saveUser(authRequest);
        emailService.sendWelcomeEmail(authRequest.getEmail(), authRequest.getUsername());

        logUserRegistration(authRequest.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registro exitoso");

        return ResponseEntity.ok(response);
    }

    private void logUserRegistration(String username) {
        String logFilePath = "registro_usuarios.txt"; // Ruta del archivo donde se guardarán los registros
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        String logEntry = String.format("Usuario: %s | Fecha y Hora: %s%n", username, timestamp);

        try (FileWriter writer = new FileWriter(logFilePath, true)) { // true para agregar al final del archivo
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de registro: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.sendPasswordResetToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Se ha enviado un enlace de restablecimiento de contraseña a tu email.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean result = passwordResetService.resetPassword(token, newPassword);

        if (result) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contraseña restablecida correctamente.");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Token inválido o expirado."));
        }
    }
}
