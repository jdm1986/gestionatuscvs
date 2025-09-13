// NotaController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Defino este controlador para gestionar las notas asociadas a los curriculums

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

// Anoto la clase como un controlador REST y defino la ruta base "/curriculums", permitiendo solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/curriculums")
public class NotaController {

    // Inyecto los servicios necesarios para gestionar las notas y los curriculums
    @Autowired
    private NotaService notaService;
    @Autowired
    private CurriculumService curriculumService;

    // Configuro un endpoint para obtener una lista de notas asociadas a un curriculum específico
    @GetMapping("/{curriculumId}/notas")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<NotaDTO> getNotasByCurriculumId(@PathVariable Long curriculumId) {
        // Obtengo las notas asociadas al curriculum y las convierto a DTO
        return notaService.getNotasByCurriculumId(curriculumId)
                .stream()
                .map(nota -> new NotaDTO(nota.getId(), nota.getContenido(), nota.getFechaCreacion()))
                .collect(Collectors.toList());
    }

    // Configuro un endpoint para agregar una nueva nota a un curriculum específico
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

    // Configuro un endpoint para eliminar una nota específica por su ID
    @DeleteMapping("/notas/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deleteNota(@PathVariable Long id) {
        // Elimino la nota indicada
        notaService.deleteNota(id);
        return ResponseEntity.ok().build();
    }
}
