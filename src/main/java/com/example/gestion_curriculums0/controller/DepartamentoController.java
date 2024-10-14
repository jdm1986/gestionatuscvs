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

    // Endpoint para obtener todos los departamentos
    @GetMapping
    public List<Departamento> getAllDepartamentos() {
        return departamentoService.getAllDepartamentos();
    }

    // Endpoint para agregar un nuevo departamento
    @PostMapping
    public ResponseEntity<?> createDepartamento(@RequestBody Departamento departamento) {
        // Verifico si el nombre del departamento es válido
        if (departamento.getNombre() == null || departamento.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del departamento es obligatorio.");
        }

        // Verifico si el departamento ya existe por su nombre
        Optional<Departamento> departamentoExistente = departamentoService.findByNombre(departamento.getNombre());
        if (departamentoExistente.isPresent()) {
            return ResponseEntity.badRequest().body("El departamento ya existe.");
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
