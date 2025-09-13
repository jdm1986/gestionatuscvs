// JwtAuthenticationFilter.java
package com.example.gestion_curriculums0.security;

import com.example.gestion_curriculums0.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/* El JwtAuthenticationFilter es un filtro que se ejecuta en cada solicitud HTTP para verificar si
la misma contiene un token JWT válido. Extrae el token desde las cookies de la solicitud y, si es válido,
autentica al usuario correspondiente, estableciendo el contexto de seguridad para la solicitud. De esta manera,
garantiza que solo los usuarios con un token válido puedan acceder a los endpoints protegidos. Este filtro es
esencial para la seguridad de la aplicación, proporcionando una capa adicional de protección para cada solicitud. */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Primero intento extraer el token desde la cookie "accessToken"
        String accessToken = jwtUtil.extractTokenFromRequest(request, "accessToken");

        // Si no lo había en la cookie, intento obtenerlo desde el header Authorization
        if (accessToken == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }
        try {
            if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.extractUsername(accessToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception ex) {
            // Si el token es inválido/expirado, limpiar cookie y continuar como anónimo
            response.addHeader("Set-Cookie", "accessToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        return path.equals("/")
                || path.equals("/favicon.ico")
                || path.endsWith(".html")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/static/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/")
                || path.startsWith("/auth/");
    }
}

