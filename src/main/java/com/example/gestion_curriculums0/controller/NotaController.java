package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.Nota;
import com.example.gestion_curriculums0.model.NotaDTO;
import com.example.gestion_curriculums0.service.CurriculumService;
import com.example.gestion_curriculums0.service.NotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Defino este controlador para gestionar las notas asociadas a los curriculums
@RestController
@RequestMapping("/curriculums")
@CrossOrigin(origins = "http://localhost:8000")
public class NotaController {

    @Autowired
    private NotaService notaService;

    @Autowired
    private CurriculumService curriculumService;

    // Obtengo una lista de notas asociadas a un curriculum específico
    @GetMapping("/{curriculumId}/notas")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<NotaDTO> getNotasByCurriculumId(@PathVariable Long curriculumId) {
        return notaService.getNotasByCurriculumId(curriculumId)
                .stream()
                .map(nota -> new NotaDTO(nota.getId(), nota.getContenido(), nota.getFechaCreacion()))
                .collect(Collectors.toList());
    }

    // Agrego una nueva nota a un curriculum específico
    @PostMapping("/{curriculumId}/notas")
    @PreAuthorize("hasAuthority('USER')")
    public NotaDTO addNota(@PathVariable Long curriculumId, @RequestBody NotaDTO notaDTO) {
        // Creo una nueva instancia de la entidad Nota y la asocio al curriculum
        Nota nota = new Nota();
        nota.setContenido(notaDTO.getContenido());
        nota.setCurriculum(curriculumService.getCurriculumById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado")));

        // Guardo la nota y devuelvo el DTO con la información guardada
        Nota savedNota = notaService.saveNota(nota);
        return new NotaDTO(savedNota.getId(), savedNota.getContenido(), savedNota.getFechaCreacion());
    }

    // Elimino una nota específica por su ID
    @DeleteMapping("/notas/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteNota(@PathVariable Long id) {
        notaService.deleteNota(id);
        return ResponseEntity.ok().build();
    }
}
