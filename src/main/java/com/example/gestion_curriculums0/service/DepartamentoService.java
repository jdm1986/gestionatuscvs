// DepartamentoService.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.service;

/*
  Este servicio me permite gestionar las operaciones con los departamentos,
  como la búsqueda, guardado, y eliminación, actuando como intermediario
  entre los controladores y el repositorio de departamentos.
 */

import com.example.gestion_curriculums0.model.Departamento;
import com.example.gestion_curriculums0.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository; // Inyecto el repositorio de departamentos

    // Método para obtener los departamentos filtrados por userId
    public List<Departamento> getDepartamentosByUserId(Long userId) {
        return departamentoRepository.findByUserId(userId);
    }

    // Método para guardar un nuevo departamento
    public Departamento saveDepartamento(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    // Método para eliminar un departamento por su ID
    public void deleteDepartamento(Long id) {
        departamentoRepository.deleteById(id);
    }

    // Método para buscar un departamento por su nombre y userId
    public Optional<Departamento> findByNombreAndUserId(String nombre, Long userId) {
        return departamentoRepository.findByNombreAndUserId(nombre, userId);
    }
}
