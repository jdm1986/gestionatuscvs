package com.example.gestion_curriculums0.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
  Clase JwtUtil: Me encargo de generar, validar y extraer información de los tokens JWT utilizados en la autenticación de usuarios.
  También gestiono la extracción de tokens desde las cookies enviadas en las solicitudes HTTP.
 */
@Service
public class JwtUtil {

    // Genero una clave secreta utilizando HS256 para firmar mis tokens JWT
    private Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Extraigo el nombre de usuario (subject) desde el token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraigo la fecha de expiración del token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Método genérico para extraer cualquier tipo de claim del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraigo todos los claims del token JWT utilizando la clave secreta
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    // Verifico si el token ha expirado comparando la fecha de expiración con la fecha actual
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Genero un nuevo token con los detalles del usuario y el tiempo de expiración
    public String generateToken(UserDetails userDetails, int expirationTimeInSeconds) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), expirationTimeInSeconds);
    }

    // Creo el token JWT asignando claims, subject (usuario) y fecha de expiración
    private String createToken(Map<String, Object> claims, String subject, int expirationTimeInSeconds) {
        return Jwts.builder()
                .setClaims(claims)  // Añado los claims al token
                .setSubject(subject)  // Establezco el subject (usuario)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Asigno la fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000))  // Establezco la expiración
                .signWith(SECRET_KEY)  // Firmo el token con la clave secreta
                .compact();  // Compacto y creo el token
    }

    // Valido el token verificando si el usuario coincide y si no ha expirado
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extraigo el token desde las cookies de la solicitud (por ejemplo, "accessToken")
    public String extractTokenFromRequest(HttpServletRequest request, String tokenName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;  // Si no encuentro el token en las cookies, retorno null
    }
}
