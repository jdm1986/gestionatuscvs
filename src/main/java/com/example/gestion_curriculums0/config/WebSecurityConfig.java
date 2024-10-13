package com.example.gestion_curriculums0.config;

// Esta clase es la encargada de configurar la seguridad de mi aplicación utilizando Spring Security.
// Aquí defino las reglas de autorización, la política de sesiones y la integración con JWT para la autenticación.

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

// Indico que esta clase es una configuración de Spring Boot que he definido para gestionar la seguridad
@Configuration
// Habilito la seguridad web en mi aplicación
@EnableWebSecurity
public class WebSecurityConfig {

    // Inyecto el filtro personalizado que he creado para manejar la autenticación JWT
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Inyecto el entorno para acceder a las variables de entorno del sistema, como mis configuraciones sensibles
    @Autowired
    private Environment environment;

    // Este bean configura la cadena de filtros de seguridad que yo he diseñado para las peticiones HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Deshabilito la protección CSRF (Cross-Site Request Forgery) porque he decidido utilizar JWT para gestionar la seguridad de las peticiones
        http.csrf(csrf -> csrf.disable());

        // Habilito CORS (Cross-Origin Resource Sharing) para que mi frontend pueda hacer peticiones a la API
        http.cors(withDefaults());

        // Aquí defino las reglas de autorización: las rutas que yo permito que sean accedidas sin autenticación y las que requieren autenticación
        http
                .authorizeHttpRequests(auth -> auth
                        // Permito el acceso sin autenticación a las rutas que he definido para autenticación y documentación
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/curriculums/generate-link").permitAll()
                        .requestMatchers("/**").permitAll()  // En desarrollo, permito todas las rutas para facilitar las pruebas
                        .anyRequest().authenticated()  // Todas las demás rutas requieren autenticación según mi configuración
                )
                // Establezco que no se deben crear sesiones (stateless), ya que utilizo JWT para gestionar las autenticaciones de los usuarios
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Añado mi filtro JWT personalizado antes del filtro estándar de autenticación de usuario y contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devuelvo la configuración completa que he definido
        return http.build();
    }

    // Configuro CORS para permitir que mi frontend (https://gestionatuscv.es) pueda hacer peticiones a la API
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permito las solicitudes desde mi dominio de producción
        configuration.addAllowedOrigin("https://gestionatuscv.es");
        // Permito todos los métodos HTTP (GET, POST, etc.) para interactuar con mi API
        configuration.addAllowedMethod("*");
        // Permito todos los encabezados de las solicitudes, ya que los necesito para la autenticación y el funcionamiento del sistema
        configuration.addAllowedHeader("*");
        // Habilito el uso de cookies en las solicitudes si es necesario para mi proyecto
        configuration.setAllowCredentials(true);

        // Aplico esta configuración de CORS a todas las rutas de mi aplicación
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Defino el gestor de autenticación, utilizando la configuración que yo he implementado para autenticar a los usuarios
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Defino el bean para la codificación de contraseñas, usando BCrypt, para mejorar la seguridad de las contraseñas de mis usuarios
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
