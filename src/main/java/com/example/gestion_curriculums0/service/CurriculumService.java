package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
  Clase CurriculumService: Me encargo de gestionar las operaciones relacionadas con los curriculums,
  como la búsqueda, guardado, eliminación y filtrado por criterios específicos. Intermediario
  entre los controladores y el repositorio que accede a la base de datos.
 */
@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository; // Inyecto el repositorio para acceder a los curriculums en la base de datos

    //Obtengo todos los curriculums almacenados en la base de datos.

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    //Busco un curriculum específico por su ID.

    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findById(id);
    }

    //Obtengo todos los curriculums asociados a un usuario por su ID.

    public List<Curriculum> getCurriculumsByUsuarioId(Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId);
    }

    //Busco curriculums que contengan el nombre proporcionado.
    public List<Curriculum> buscarPorNombre(String nombre) {
        return curriculumRepository.findByNombreContaining(nombre);
    }

    //Busco curriculums que contengan el apellido proporcionado.

    public List<Curriculum> buscarPorApellido(String apellido) {
        return curriculumRepository.findByApellidoContaining(apellido);
    }

    /*
      Realizo una búsqueda de curriculums por palabras clave y el ID del usuario, buscando coincidencias
      en el nombre, apellido, texto del CV, o sexo.
     */
    public List<Curriculum> buscarPorClaveYUsuario(String clave, Long usuarioId) {
        String[] keywords = clave.split(" "); // Divido las palabras clave para buscar una por una
        List<Curriculum> allCurriculums = curriculumRepository.findByUsuarioId(usuarioId); // Obtengo los curriculums del usuario
        List<Curriculum> matchingCurriculums = new ArrayList<>();

        // Recorro cada curriculum y verifico si contiene alguna de las palabras clave
        for (Curriculum curriculum : allCurriculums) {
            boolean matches = true;
            for (String keyword : keywords) {
                if (!(curriculum.getNombre().toLowerCase().contains(keyword.toLowerCase()) ||
                        curriculum.getApellido().toLowerCase().contains(keyword.toLowerCase()) ||
                        curriculum.getCvBruto().toLowerCase().contains(keyword.toLowerCase()) ||
                        curriculum.getSexo().toLowerCase().contains(keyword.toLowerCase()))) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                matchingCurriculums.add(curriculum); // Agrego el curriculum que cumple con los criterios
            }
        }
        return matchingCurriculums;
    }

    //Guardo un nuevo curriculum o actualizo uno existente en la base de datos.

    public Curriculum saveCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    //Elimino un curriculum por su ID
    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }
}
