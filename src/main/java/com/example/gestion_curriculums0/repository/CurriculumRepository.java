// CurriculumRepository.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.repository;

// Declaro la interfaz CurriculumRepository que extiende JpaRepository para manejar las operaciones CRUD de la entidad Curriculum

import com.example.gestion_curriculums0.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    // Defino un método para buscar curriculums cuyo nombre contenga una cadena específica
    List<Curriculum> findByNombreContaining(String nombre);

    // Defino un método para buscar curriculums cuyo apellido contenga una cadena específica
    List<Curriculum> findByApellidoContaining(String apellido);

    // Defino un método para obtener todos los curriculums asociados a un usuario en particular por su ID
    List<Curriculum> findByUsuarioId(Long usuarioId);

    // Defino un método para buscar en el contenido bruto del currículum (cvBruto) una palabra clave, filtrando por el usuario
    List<Curriculum> findByCvBrutoContainingAndUsuarioId(String clave, Long usuarioId);

    // Defino un método para eliminar todos los curriculums asociados a un usuario por su ID
    void deleteByUsuarioId(Long usuarioId);
}
