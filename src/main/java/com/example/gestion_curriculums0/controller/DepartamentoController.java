// DepartamentoController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

// Este controlador me permite gestionar las peticiones HTTP relacionadas con los departamentos

import com.example.gestion_curriculums0.model.DepartamentoDTO;
import com.example.gestion_curriculums0.model.Departamento;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.service.DepartamentoService;
import com.example.gestion_curriculums0.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Anoto la clase como un controlador REST y defino la ruta base "/departamentos", permitiendo solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    // Inyecto los servicios necesarios para gestionar departamentos y usuarios
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private UsuarioService usuarioService;

    // Configuro un endpoint para obtener los departamentos asociados a un usuario específico
    @GetMapping
    public List<DepartamentoDTO> getDepartamentosByUserId(@RequestParam Long userId) {
        // Filtro los departamentos por userId y los convierto a DTO
        return departamentoService.getDepartamentosByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Configuro un endpoint para agregar un nuevo departamento
    @PostMapping
    public ResponseEntity<?> createDepartamento(@RequestBody DepartamentoDTO departamentoDTO) {
        // Obtengo el userId del DTO
        Long userId = departamentoDTO.getUserId();

        // Verifico si el usuario existe
        Optional<Usuario> usuario = usuarioService.findById(userId);
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("El usuario no existe.");
        }

        // Verifico si el departamento ya existe para el usuario
        Optional<Departamento> departamentoExistente = departamentoService.findByNombreAndUserId(departamentoDTO.getNombre(), userId);
        if (departamentoExistente.isPresent()) {
            return ResponseEntity.badRequest().body("El departamento ya existe para este usuario.");
        }

        // Creo una nueva entidad Departamento a partir del DTO y asigno el usuario
        Departamento departamento = new Departamento();
        departamento.setNombre(departamentoDTO.getNombre().toLowerCase());
        departamento.setUsuario(usuario.get());

        // Guardo el nuevo departamento y devuelvo el DTO correspondiente
        Departamento savedDepartamento = departamentoService.saveDepartamento(departamento);
        return ResponseEntity.ok(convertToDTO(savedDepartamento));
    }

    // Configuro un endpoint para eliminar un departamento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartamento(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
        return ResponseEntity.ok().build();
    }

    // Método privado para convertir una entidad Departamento en un DepartamentoDTO
    private DepartamentoDTO convertToDTO(Departamento departamento) {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setId(departamento.getId());
        dto.setNombre(departamento.getNombre());
        dto.setUserId(departamento.getUsuario().getId());
        return dto;
    }
}
