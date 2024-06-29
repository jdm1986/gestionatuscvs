package com.example.gestion_curriculums0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculums")
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    @GetMapping("/{id}")
    public Curriculum getCurriculumById(@PathVariable Long id) {
        return curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Curriculum> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/buscar/nombre")
    public List<Curriculum> buscarPorNombre(@RequestParam String nombre) {
        return curriculumRepository.findByNombreContaining(nombre);
    }

    @GetMapping("/buscar/apellido")
    public List<Curriculum> buscarPorApellido(@RequestParam String apellido) {
        return curriculumRepository.findByApellidoContaining(apellido);
    }

    @GetMapping("/buscar/etiqueta")
    public List<Curriculum> buscarPorEtiqueta(@RequestParam String etiqueta) {
        return curriculumRepository.findByEtiquetasContaining(etiqueta);
    }

    @PostMapping
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum, @RequestParam Long userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario not found"));
        curriculum.setUsuario(usuario);
        return curriculumRepository.save(curriculum);
    }

    @PutMapping("/{id}")
    public Curriculum updateCurriculum(@PathVariable Long id, @RequestBody Curriculum updatedCurriculum) {
        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculum.setNombre(updatedCurriculum.getNombre());
        curriculum.setApellido(updatedCurriculum.getApellido());
        curriculum.setPdfPath(updatedCurriculum.getPdfPath());
        curriculum.setEtiquetas(updatedCurriculum.getEtiquetas());
        return curriculumRepository.save(curriculum);
    }

    @DeleteMapping("/{id}")
    public void deleteCurriculum(@PathVariable Long id) {
        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculumRepository.delete(curriculum);
    }
}
