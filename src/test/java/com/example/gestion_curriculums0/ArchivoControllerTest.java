package com.example.gestion_curriculums0;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArchivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurriculumRepository curriculumRepository;

    @MockBean
    private MockOpenAiService openAiService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser // Simula un usuario autenticado
    void testSubirArchivo() throws Exception {
        Curriculum curriculum = new Curriculum();
        curriculum.setId(1L);
        curriculum.setNombre("John");
        curriculum.setApellido("Doe");

        when(curriculumRepository.findById(1L)).thenReturn(java.util.Optional.of(curriculum));
        when(openAiService.getCvSummary(any(String.class))).thenReturn("Resumen del CV");

        File file = new File("src/test/resources/test.pdf");
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("archivo", "test.pdf", "application/pdf", input);

        mockMvc.perform(multipart("/archivos/subir/1").file(mockMultipartFile))
                .andExpect(status().isOk());
    }
}


//Para quepueda usar el Servicio Open Ai

//package com.example.gestion_curriculums0;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.io.File;
//import java.io.FileInputStream;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class ArchivoControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CurriculumRepository curriculumRepository;
//
//    @MockBean
//    private CustomOpenAiService openAiService;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    @Test
//    @WithMockUser // Simula un usuario autenticado
//    void testSubirArchivo() throws Exception {
//        Curriculum curriculum = new Curriculum();
//        curriculum.setId(1L);
//        curriculum.setNombre("John");
//        curriculum.setApellido("Doe");
//
//        when(curriculumRepository.findById(1L)).thenReturn(java.util.Optional.of(curriculum));
//        when(openAiService.getCvSummary(any(String.class))).thenReturn("Resumen del CV");
//
//        File file = new File("src/test/resources/test.pdf");
//        FileInputStream input = new FileInputStream(file);
//        MockMultipartFile mockMultipartFile = new MockMultipartFile("archivo", "test.pdf", "application/pdf", input);
//
//        mockMvc.perform(multipart("/archivos/subir/1").file(mockMultipartFile))
//                .andExpect(status().isOk());
//    }
//}
