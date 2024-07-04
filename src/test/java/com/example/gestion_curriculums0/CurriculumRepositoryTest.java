package com.example.gestion_curriculums0;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CurriculumRepositoryTest {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Test
    public void testFechaInsercion() {
        Curriculum curriculum = new Curriculum();
        curriculum.setApellido("Doe");
        curriculum.setNombre("John");
        curriculum.setPdfPath("path/to/pdf");
        curriculum.setResumenCv("Este es un resumen");
        curriculum.setSexo("Masculino");
        curriculum.setTelefono("123456789");
        curriculum.setEmail("john.doe@example.com");

        Curriculum savedCurriculum = curriculumRepository.save(curriculum);

        assertThat(savedCurriculum.getFechaInsercion()).isNotNull();
        assertThat(ChronoUnit.SECONDS.between(savedCurriculum.getFechaInsercion(), LocalDateTime.now()) < 5).isTrue();
    }
}
