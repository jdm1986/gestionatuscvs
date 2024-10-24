// JwtAuthenticationFilter.java
// Indico el paquete al que pertenece esta clase
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

        // Extraigo el token JWT desde la cookie "accessToken" en la solicitud
        String accessToken = jwtUtil.extractTokenFromRequest(request, "accessToken");

        // Verifico si tengo un token válido y si el contexto de seguridad no está autenticado
        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extraigo el nombre de usuario desde el token JWT
            String username = jwtUtil.extractUsername(accessToken);

            // Cargo los detalles del usuario desde mi servicio personalizado
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Verifico si el token es válido comparándolo con los detalles del usuario
            if (jwtUtil.validateToken(accessToken, userDetails)) {
                // Si el token es válido, creo un objeto de autenticación basado en los detalles del usuario
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Asocio detalles adicionales de la solicitud a la autenticación
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establezco el objeto de autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continúo con la cadena de filtros, pasando la solicitud y respuesta al siguiente filtro
        filterChain.doFilter(request, response);
    }
}
