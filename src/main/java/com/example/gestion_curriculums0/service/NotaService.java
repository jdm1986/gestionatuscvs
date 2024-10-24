// NotaService.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.service;

/*
  Esta clase gestiona la lógica de negocio relacionada con las notas. Proporciona métodos
  para obtener notas asociadas a un currículum específico, guardar una nueva nota y eliminar
  notas de la base de datos.
 */

import com.example.gestion_curriculums0.model.Nota;
import com.example.gestion_curriculums0.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaService {

    @Autowired
    private NotaRepository notaRepository; // Inyecto el repositorio de notas para acceder a la base de datos

    // En este método obtengo todas las notas asociadas a un currículum específico
    public List<Nota> getNotasByCurriculumId(Long curriculumId) {
        return notaRepository.findByCurriculumId(curriculumId);
    }

    // Aquí guardo una nueva nota en la base de datos
    public Nota saveNota(Nota nota) {
        return notaRepository.save(nota);
    }

    // Este método elimina una nota específica de la base de datos
    public void deleteNota(Long id) {
        notaRepository.deleteById(id);
    }
}
