// WebSecurityConfig.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.config;

// Esta clase es la encargada de configurar la seguridad de mi aplicación utilizando Spring Security
// Aquí defino las reglas de autorización, la política de sesiones y la integración con JWT para la autenticación

import com.example.gestion_curriculums0.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

// Con la anotación @Configuration, indico que esta clase es una configuración de Spring Boot para gestionar la seguridad
@Configuration
// Habilito la seguridad web en mi aplicación con @EnableWebSecurity
@EnableWebSecurity
public class WebSecurityConfig {

    // Inyecto el filtro personalizado que he creado para manejar la autenticación JWT
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Inyecto el entorno para acceder a las variables de entorno del sistema, como mis configuraciones sensibles
    @Autowired
    private Environment environment;

    // Configuro la cadena de filtros de seguridad que he diseñado para las peticiones HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Deshabilito la protección CSRF porque utilizo JWT para la seguridad de las peticiones
        http.csrf(csrf -> csrf.disable());

        // Habilito CORS para que mi frontend pueda hacer peticiones a la API
        http.cors(withDefaults());

        // Defino las reglas de autorización: las rutas permitidas sin autenticación y las que requieren autenticación
        http
                .authorizeHttpRequests(auth -> auth
                        // Permito el acceso sin autenticación a las rutas que he definido para autenticación y documentación
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/curriculums/generate-link").permitAll()
                        .requestMatchers("/**").permitAll()  // En desarrollo, permito todas las rutas para facilitar las pruebas
                        .anyRequest().authenticated()  // Requiero autenticación para cualquier otra ruta
                )
                // Establezco la política de sesiones como "stateless" ya que utilizo JWT para la autenticación
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Añado mi filtro JWT personalizado antes del filtro estándar de autenticación
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devuelvo la configuración completa
        return http.build();
    }

    // Configuro CORS para permitir que mi frontend pueda hacer peticiones a la API
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permito solicitudes desde el dominio del frontend desplegado en Railway
        configuration.addAllowedOrigin("https://gestionatuscvs-production.up.railway.app");
        // Permito también desde el dominio original gestionatuscv.es por si usas frontend separado
        configuration.addAllowedOrigin("https://gestionatuscv.es");
        // Permito todos los métodos HTTP
        configuration.addAllowedMethod("*");
        // Permito todos los encabezados de las solicitudes
        configuration.addAllowedHeader("*");
        // Habilito el uso de cookies en las solicitudes si es necesario
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Defino el gestor de autenticación utilizando mi configuración para autenticar usuarios
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configuro el bean para la codificación de contraseñas usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

