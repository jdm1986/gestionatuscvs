package com.example.gestion_curriculums0;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        Usuario user = new Usuario();
        user.setNombreUsuario("john");
        user.setPassword(new BCryptPasswordEncoder().encode("12345"));
        user.setEmail("john@example.com");
        user.setRoles("USER");

        when(usuarioRepository.findByNombreUsuario("john")).thenReturn(user);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRegistrarUsuario() throws Exception {
        String json = "{\"nombreUsuario\":\"john\", \"password\":\"12345\", \"email\":\"john@example.com\", \"roles\":\"USER\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginUsuario() throws Exception {
        String json = "{\"username\":\"john\", \"password\":\"12345\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }
}
