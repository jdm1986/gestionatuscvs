// JwtUtil.java
package com.example.gestion_curriculums0.security;

/*
  Clase JwtUtil: Genera, valida y extrae información de tokens JWT.
  También permite extraer tokens desde cookies en las solicitudes HTTP.
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final Key SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret:}") String secret) {
        Key key;
        if (secret != null && !secret.trim().isEmpty()) {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(secret);
                key = Keys.hmacShaKeyFor(keyBytes);
            } catch (IllegalArgumentException ex) {
                key = Keys.hmacShaKeyFor(secret.getBytes());
            }
        } else {
            key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        this.SECRET_KEY = key;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, int expirationTimeInSeconds) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), expirationTimeInSeconds);
    }

    private String createToken(Map<String, Object> claims, String subject, int expirationTimeInSeconds) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000L))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractTokenFromRequest(HttpServletRequest request, String tokenName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

