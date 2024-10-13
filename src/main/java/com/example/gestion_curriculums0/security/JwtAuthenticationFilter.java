//Esta clase es un filtro que se encarga de verificar si una solicitud HTTP
// contiene un token JWT (JSON Web Token) válido. Este filtro se ejecuta en cada solicitud
// que pase por él, y su principal función es autenticar al usuario basado en ese token.

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
