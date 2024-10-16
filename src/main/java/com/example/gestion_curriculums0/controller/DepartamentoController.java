// Este controlador me permite gestionar las peticiones HTTP relacionadas con los departamentos
package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.Departamento;
import com.example.gestion_curriculums0.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departamentos")
@CrossOrigin(origins = "http://localhost:8000")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService;

    // Endpoint para obtener los departamentos de un usuario específico
    @GetMapping
    public List<Departamento> getDepartamentosByUserId(@RequestParam Long userId) {
        return departamentoService.getDepartamentosByUserId(userId); // Filtrar departamentos por userId
    }

    // Endpoint para agregar un nuevo departamento
    @PostMapping
    public ResponseEntity<?> createDepartamento(@RequestBody Departamento departamento) {
        // Aquí accedo al departamento.getUserId() directamente desde el cuerpo de la petición
        Long userId = departamento.getUserId();

        // Verifico si el departamento ya existe para el usuario
        Optional<Departamento> departamentoExistente = departamentoService.findByNombreAndUserId(departamento.getNombre(), userId);
        if (departamentoExistente.isPresent()) {
            return ResponseEntity.badRequest().body("El departamento ya existe para este usuario.");
        }

        // Si no existe, lo guardo
        return ResponseEntity.ok(departamentoService.saveDepartamento(departamento));
    }


    // Endpoint para eliminar un departamento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartamento(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
        return ResponseEntity.ok().build();
    }
}

