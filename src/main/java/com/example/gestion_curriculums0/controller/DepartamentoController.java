// Este controlador me permite gestionar las peticiones HTTP relacionadas con los departamentos
package com.example.gestion_curriculums0.controller;

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

@RestController
@RequestMapping("/departamentos")
@CrossOrigin(origins = "http://localhost:8000")
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService; // Servicio para gestionar los departamentos

    @Autowired
    private UsuarioService usuarioService; // Servicio para gestionar los usuarios

    // Endpoint para obtener los departamentos de un usuario específico
    @GetMapping
    public List<DepartamentoDTO> getDepartamentosByUserId(@RequestParam Long userId) {
        // Filtra los departamentos por userId y los convierte a DepartamentoDTO
        return departamentoService.getDepartamentosByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Endpoint para agregar un nuevo departamento
    @PostMapping
    public ResponseEntity<?> createDepartamento(@RequestBody DepartamentoDTO departamentoDTO) {
        // Obtengo el userId del DTO
        Long userId = departamentoDTO.getUserId();

        // Verificar si el usuario existe
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
        departamento.setNombre(departamentoDTO.getNombre());
        departamento.setUsuario(usuario.get());

        // Guardo el nuevo departamento y devuelvo el DTO correspondiente
        Departamento savedDepartamento = departamentoService.saveDepartamento(departamento);
        return ResponseEntity.ok(convertToDTO(savedDepartamento));
    }

    // Endpoint para eliminar un departamento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartamento(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
        return ResponseEntity.ok().build();
    }

    // Método privado para convertir una entidad Departamento a DepartamentoDTO
    private DepartamentoDTO convertToDTO(Departamento departamento) {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setId(departamento.getId());
        dto.setNombre(departamento.getNombre());
        dto.setUserId(departamento.getUsuario().getId());
        return dto;
    }
}
