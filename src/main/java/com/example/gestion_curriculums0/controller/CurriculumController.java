package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.CurriculumDTO;
import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/curriculums")
@CrossOrigin(origins = "http://localhost:8000")
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @ApiOperation(value = "View a list of available curriculums", response = List.class)
    @GetMapping
    @Transactional(readOnly = true)
    public List<CurriculumDTO> getAllCurriculums() {
        return curriculumRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CurriculumDTO getCurriculumById(@PathVariable Long id) {
        return curriculumRepository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    public List<CurriculumDTO> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/nombre")
    @Transactional(readOnly = true)
    public List<CurriculumDTO> buscarPorNombre(@RequestParam String nombre) {
        return curriculumRepository.findByNombreContaining(nombre).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/apellido")
    @Transactional(readOnly = true)
    public List<CurriculumDTO> buscarPorApellido(@RequestParam String apellido) {
        return curriculumRepository.findByApellidoContaining(apellido).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping
    public CurriculumDTO createCurriculum(@RequestBody CurriculumDTO curriculumDTO, @RequestParam Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));
        Curriculum curriculum = new Curriculum();
        curriculum.setNombre(curriculumDTO.getNombre());
        curriculum.setApellido(curriculumDTO.getApellido());
        curriculum.setPdfPath(curriculumDTO.getPdfPath());
        curriculum.setSexo(curriculumDTO.getSexo());
        curriculum.setTelefono(curriculumDTO.getTelefono());
        curriculum.setEmail(curriculumDTO.getEmail());
        curriculum.setUsuario(usuario);
        return convertToDTO(curriculumRepository.save(curriculum));
    }

    @PutMapping("/{id}")
    @Transactional
    public CurriculumDTO updateCurriculum(@PathVariable Long id, @RequestBody CurriculumDTO updatedCurriculumDTO) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculum.setNombre(updatedCurriculumDTO.getNombre());
        curriculum.setApellido(updatedCurriculumDTO.getApellido());
        curriculum.setPdfPath(updatedCurriculumDTO.getPdfPath());
        curriculum.setSexo(updatedCurriculumDTO.getSexo());
        curriculum.setTelefono(updatedCurriculumDTO.getTelefono());
        curriculum.setEmail(updatedCurriculumDTO.getEmail());
        return convertToDTO(curriculumRepository.save(curriculum));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteCurriculum(@PathVariable Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculumRepository.delete(curriculum);
    }

    @GetMapping("/buscar/avanzado")
    @Transactional(readOnly = true)
    public Page<CurriculumDTO> buscarAvanzado(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return curriculumRepository.findByAdvancedSearch(nombre, apellido, pageable)
                .map(this::convertToDTO);
    }

    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(), curriculum.getNombre(), curriculum.getApellido(), curriculum.getPdfPath(),
                curriculum.getResumenCv(), curriculum.getSexo(), curriculum.getTelefono(), curriculum.getEmail(),
                null); // Pasa null para las etiquetas
    }
}
