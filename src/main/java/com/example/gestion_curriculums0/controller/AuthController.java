package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.AuthRequest;
import com.example.gestion_curriculums0.service.PasswordResetService;
import com.example.gestion_curriculums0.service.CustomUserDetailsService;
import com.example.gestion_curriculums0.service.EmailService;
import com.example.gestion_curriculums0.service.UserLogService;
import com.example.gestion_curriculums0.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8000")
public class AuthController {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    private static final int MAX_RECOVERY_ATTEMPTS = 5;
    private static final long IP_LOCK_TIME_DURATION = 60 * 60 * 1000; // 1 hour

    private Map<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private Map<String, Long> lockTime = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> registrationAttempts = new ConcurrentHashMap<>();
    private Map<String, Long> registrationLockTime = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> recoveryAttemptsByIp = new ConcurrentHashMap<>();
    private Map<String, Long> ipLockTime = new ConcurrentHashMap<>();

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

    @Autowired
    private UserLogService userLogService; // Inyectar UserLogService

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws AuthenticationException {
        String username = authRequest.getUsername();

        if (isAccountLocked(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La cuenta está bloqueada debido a múltiples intentos fallidos. Inténtalo más tarde.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);
            Long userId = userDetailsService.getUserIdByUsername(userDetails.getUsername());

            // Restablecer el contador de intentos fallidos si el inicio de sesión es exitoso
            loginAttempts.remove(username);
            lockTime.remove(username);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            incrementFailedAttempts(username);
            if (userDetailsService.userExists(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta. ¿Olvidaste tu contraseña?");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe.");
            }
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        String email = authRequest.getEmail();

        if (isRegistrationLocked(email)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos fallidos de registro. Inténtalo más tarde.");
        }

        try {
            if (userDetailsService.userExistsByEmail(email)) {
                incrementRegistrationAttempts(email);
                int attemptsLeft = MAX_FAILED_ATTEMPTS - registrationAttempts.get(email).get();
                if (attemptsLeft > 0) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("El correo electrónico ya está registrado. Te quedan " + attemptsLeft + " intentos.");
                } else {
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                            .body("Demasiados intentos fallidos. Redirigiendo a la recuperación de contraseña...");
                }
            }

            userDetailsService.saveUser(authRequest);
            emailService.sendWelcomeEmail(authRequest.getEmail(), authRequest.getUsername());
            emailService.sendNotificationToAdmin(authRequest.getUsername(), authRequest.getEmail());

            // Restablecer el contador de intentos fallidos si el registro es exitoso
            registrationAttempts.remove(email);
            registrationLockTime.remove(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registro exitoso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            incrementRegistrationAttempts(email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El registro ha fallado. " + e.getMessage());
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String email = request.get("email");
        String clientIp = httpRequest.getRemoteAddr();

        if (isIpBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Collections.singletonMap("message", "Demasiados intentos fallidos desde esta IP. Inténtalo más tarde."));
        }

        try {
            if (!userDetailsService.userExistsByEmail(email)) {
                incrementRecoveryAttempts(clientIp);
                int attemptsLeft = MAX_RECOVERY_ATTEMPTS - recoveryAttemptsByIp.get(clientIp).get();
                String responseMessage = (attemptsLeft > 0)
                        ? "El correo no está registrado. Te quedan " + attemptsLeft + " intentos."
                        : "Tu IP ha sido bloqueada debido a múltiples intentos fallidos.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", responseMessage));
            }

            // Logging antes de enviar el correo
            System.out.println("Enviando token de restablecimiento de contraseña para el email: " + email);
            passwordResetService.sendPasswordResetToken(email);
            // Logging después de enviar el correo
            System.out.println("Token enviado para el email: " + email);

            resetRecoveryAttempts(clientIp);

            return ResponseEntity.ok(Collections.singletonMap("message", "Se ha enviado un enlace de restablecimiento de contraseña a tu email."));
        } catch (Exception e) {
            incrementRecoveryAttempts(clientIp);
            int attemptsLeft = MAX_RECOVERY_ATTEMPTS - recoveryAttemptsByIp.get(clientIp).get();
            String responseMessage = (attemptsLeft > 0)
                    ? "Error al enviar el email de restablecimiento. Te quedan " + attemptsLeft + " intentos."
                    : "Tu IP ha sido bloqueada debido a múltiples intentos fallidos.";

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", responseMessage));
        }
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

    private void incrementFailedAttempts(String identifier) {
        loginAttempts.putIfAbsent(identifier, new AtomicInteger(0));
        int attempts = loginAttempts.get(identifier).incrementAndGet();
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockTime.put(identifier, System.currentTimeMillis());
        }
    }

    private void incrementRegistrationAttempts(String identifier) {
        registrationAttempts.putIfAbsent(identifier, new AtomicInteger(0));
        int attempts = registrationAttempts.get(identifier).incrementAndGet();
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            registrationLockTime.put(identifier, System.currentTimeMillis());
        }
    }

    private boolean isAccountLocked(String identifier) {
        Long lockTimeStart = lockTime.get(identifier);
        if (lockTimeStart != null) {
            long timePassed = System.currentTimeMillis() - lockTimeStart;
            if (timePassed > LOCK_TIME_DURATION) {
                lockTime.remove(identifier);
                loginAttempts.remove(identifier);
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isRegistrationLocked(String identifier) {
        Long lockTimeStart = registrationLockTime.get(identifier);
        if (lockTimeStart != null) {
            long timePassed = System.currentTimeMillis() - lockTimeStart;
            if (timePassed > LOCK_TIME_DURATION) {
                registrationLockTime.remove(identifier);
                registrationAttempts.remove(identifier);
                return false;
            }
            return true;
        }
        return false;
    }

    private void incrementRecoveryAttempts(String ip) {
        recoveryAttemptsByIp.putIfAbsent(ip, new AtomicInteger(0));
        int attempts = recoveryAttemptsByIp.get(ip).incrementAndGet();
        if (attempts >= MAX_RECOVERY_ATTEMPTS) {
            ipLockTime.put(ip, System.currentTimeMillis());
        }
    }

    private boolean isIpBlocked(String ip) {
        Long lockTimeStart = ipLockTime.get(ip);
        if (lockTimeStart != null) {
            long timePassed = System.currentTimeMillis() - lockTimeStart;
            if (timePassed > IP_LOCK_TIME_DURATION) {
                ipLockTime.remove(ip);
                recoveryAttemptsByIp.remove(ip);
                return false;
            }
            return true;
        }
        return false;
    }

    private void resetRecoveryAttempts(String ip) {
        recoveryAttemptsByIp.remove(ip);
        ipLockTime.remove(ip);
    }

    private void logUserRegistration(String username) {
        // Guardar el registro en la base de datos en lugar de un archivo de texto
        userLogService.saveUserLog(username, "Registro de usuario");
    }
}
