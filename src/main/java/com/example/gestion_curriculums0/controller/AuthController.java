// Este controlador gestiona la autenticación de usuarios, incluyendo el login, registro, manejo de tokens
// y restablecimiento de contraseñas.

package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.service.*;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Defino que este controlador está asociado a la ruta "/auth" y maneja la autenticación
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:8000")
public class AuthController {

    // Defino las constantes de tiempo para los tokens y bloqueo de cuenta
    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15 minutos
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7 días
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 horas
    private static final int MAX_RECOVERY_ATTEMPTS = 5;
    private static final long IP_LOCK_TIME_DURATION = 60 * 60 * 1000; // 1 hora

    // Uso mapas concurrentes para gestionar los intentos de login, registros y recuperación de contraseñas
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
    private UserLogService userLogService;

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para manejar el login de usuarios y la generación de tokens de acceso y refresco
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody AuthRequest authRequest) throws AuthenticationException {
        String username = authRequest.getUsername();

        // Verifico si la cuenta del usuario está bloqueada por múltiples intentos fallidos
        if (isAccountLocked(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La cuenta está bloqueada debido a múltiples intentos fallidos. Inténtalo más tarde.");
        }

        try {
            // Autentico al usuario usando su nombre de usuario y contraseña
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Genero los tokens de acceso y refresco
            String jwt = jwtUtil.generateToken(userDetails, ACCESS_TOKEN_EXPIRATION);
            String refreshToken = jwtUtil.generateToken(userDetails, REFRESH_TOKEN_EXPIRATION);
            Long userId = userDetailsService.getUserIdByUsername(userDetails.getUsername());

            // Creo cookies para los tokens y las añado a la respuesta
            Cookie accessTokenCookie = createCookie("accessToken", jwt, ACCESS_TOKEN_EXPIRATION);
            Cookie refreshTokenCookie = createCookie("refreshToken", refreshToken, REFRESH_TOKEN_EXPIRATION);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Reinicio los contadores de intentos fallidos de login
            loginAttempts.remove(username);
            lockTime.remove(username);

            // Devuelvo la respuesta con los detalles del usuario autenticado
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("username", userDetails.getUsername());
            responseBody.put("userId", userId);

            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            // Manejo los intentos fallidos de login incrementando el contador
            incrementFailedAttempts(username);
            if (userDetailsService.userExists(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta. ¿Olvidaste tu contraseña?");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no existe.");
            }
        }
    }

    // Endpoint para manejar la renovación del token de acceso usando el token de refresco
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Extraigo el token de refresco de las cookies
        String refreshToken = jwtUtil.extractTokenFromRequest(request, "refreshToken");

        if (refreshToken != null) {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Valido el token de refresco y genero un nuevo token de acceso si es válido
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails, ACCESS_TOKEN_EXPIRATION);
                Cookie newAccessTokenCookie = createCookie("accessToken", newAccessToken, ACCESS_TOKEN_EXPIRATION);
                response.addCookie(newAccessTokenCookie);

                return ResponseEntity.ok(Collections.singletonMap("message", "Token renovado con éxito"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido o expirado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token no encontrado.");
        }
    }

    // Endpoint para manejar el registro de nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        String email = authRequest.getEmail();
        String password = authRequest.getPassword();

        // Verifico si el registro está bloqueado por demasiados intentos fallidos
        if (isRegistrationLocked(email)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Demasiados intentos fallidos de registro. Inténtalo más tarde.");
        }

        try {
            // Verifico si el correo ya está registrado
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

            // Valido la contraseña antes de registrar al usuario
            usuarioService.validatePassword(password);

            // Guardo al nuevo usuario y envío correos de bienvenida
            userDetailsService.saveUser(authRequest);
            emailService.sendWelcomeEmail(authRequest.getEmail(), authRequest.getUsername());
            emailService.sendNotificationToAdmin(authRequest.getUsername(), authRequest.getEmail());

            // Reinicio los contadores de intentos de registro
            registrationAttempts.remove(email);
            registrationLockTime.remove(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Registro exitoso");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el registro: " + e.getMessage());
        } catch (Exception e) {
            incrementRegistrationAttempts(email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El registro ha fallado. " + e.getMessage());
        }
    }

    // Endpoint para manejar la recuperación de contraseñas
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String email = request.get("email");
        String clientIp = httpRequest.getRemoteAddr();

        // Verifico si la IP del cliente está bloqueada por demasiados intentos de recuperación
        if (isIpBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Collections.singletonMap("message", "Demasiados intentos fallidos desde esta IP. Inténtalo más tarde."));
        }

        try {
            // Verifico si el correo electrónico está registrado
            if (!userDetailsService.userExistsByEmail(email)) {
                incrementRecoveryAttempts(clientIp);
                int attemptsLeft = MAX_RECOVERY_ATTEMPTS - recoveryAttemptsByIp.get(clientIp).get();
                String responseMessage = (attemptsLeft > 0)
                        ? "El correo no está registrado. Te quedan " + attemptsLeft + " intentos."
                        : "Tu IP ha sido bloqueada debido a múltiples intentos fallidos.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", responseMessage));
            }

            // Envío el token de recuperación de contraseña
            passwordResetService.sendPasswordResetToken(email);
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

    // Endpoint para manejar el restablecimiento de contraseñas con un token de recuperación
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

    // Método auxiliar para crear cookies
    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    // Método para incrementar los intentos fallidos de login y bloquear la cuenta si se exceden los intentos permitidos
    private void incrementFailedAttempts(String identifier) {
        loginAttempts.putIfAbsent(identifier, new AtomicInteger(0));
        int attempts = loginAttempts.get(identifier).incrementAndGet();
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockTime.put(identifier, System.currentTimeMillis());
        }
    }

    // Método para incrementar los intentos fallidos de registro
    private void incrementRegistrationAttempts(String identifier) {
        registrationAttempts.putIfAbsent(identifier, new AtomicInteger(0));
        int attempts = registrationAttempts.get(identifier).incrementAndGet();
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            registrationLockTime.put(identifier, System.currentTimeMillis());
        }
    }

    // Método para verificar si una cuenta de usuario está bloqueada debido a intentos fallidos
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

    // Método para verificar si el registro de una cuenta está bloqueado
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

    // Método para incrementar los intentos fallidos de recuperación de contraseña por IP
    private void incrementRecoveryAttempts(String ip) {
        recoveryAttemptsByIp.putIfAbsent(ip, new AtomicInteger(0));
        int attempts = recoveryAttemptsByIp.get(ip).incrementAndGet();
        if (attempts >= MAX_RECOVERY_ATTEMPTS) {
            ipLockTime.put(ip, System.currentTimeMillis());
        }
    }

    // Método para verificar si una IP está bloqueada debido a intentos fallidos
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

    // Método para reiniciar los intentos fallidos de recuperación de contraseña
    private void resetRecoveryAttempts(String ip) {
        recoveryAttemptsByIp.remove(ip);
        ipLockTime.remove(ip);
    }
}
