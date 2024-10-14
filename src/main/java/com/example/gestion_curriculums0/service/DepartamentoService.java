// Este servicio me permite gestionar las operaciones con los departamentos
package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Departamento;
import com.example.gestion_curriculums0.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    // Método para obtener todos los departamentos
    public List<Departamento> getAllDepartamentos() {
        return departamentoRepository.findAll();
    }

    // Método para guardar un nuevo departamento
    public Departamento saveDepartamento(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    // Método para eliminar un departamento por su ID
    public void deleteDepartamento(Long id) {
        departamentoRepository.deleteById(id);
    }

    // Método para buscar un departamento por su ID
    public Optional<Departamento> getDepartamentoById(Long id) {
        return departamentoRepository.findById(id);
    }

    // Método para buscar un departamento por su nombre
    public Optional<Departamento> findByNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre);
    }
}
