package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.CurriculumRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.PdfService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Autowired
    private PdfService pdfService;

    @ApiOperation(value = "View a list of available curriculums", response = List.class)
    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getAllCurriculums() {
        return curriculumRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public CurriculumDTO getCurriculumById(@PathVariable Long id) {
        return curriculumRepository.findById(id).map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumRepository.findByUsuarioId(usuarioId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/nombre")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorNombre(@RequestParam String nombre) {
        return curriculumRepository.findByNombreContaining(nombre).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/apellido")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorApellido(@RequestParam String apellido) {
        return curriculumRepository.findByApellidoContaining(apellido).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CurriculumDTO uploadCurriculum(@RequestParam("file") MultipartFile file,
                                          @RequestParam Long userId,
                                          @RequestParam String nombre,
                                          @RequestParam String apellido,
                                          @RequestParam String sexo,
                                          @RequestParam String telefono,
                                          @RequestParam String email) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario not found"));

        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error converting file", e);
        }

        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(convFile);
        } catch (IOException e) {
            throw new RuntimeException("Error extracting text from PDF", e);
        }

        Curriculum curriculum = new Curriculum();
        curriculum.setNombre(nombre);
        curriculum.setApellido(apellido);
        curriculum.setPdfPath(convFile.getPath());
        curriculum.setCvBruto(extractedText);
        curriculum.setSexo(sexo);
        curriculum.setTelefono(telefono);
        curriculum.setEmail(email);
        curriculum.setUsuario(usuario);

        return convertToDTO(curriculumRepository.save(curriculum));
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCurriculum(@PathVariable Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));
        curriculumRepository.delete(curriculum);
    }

    @GetMapping("/buscar/avanzado")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
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
                curriculum.getResumenCv(), curriculum.getSexo(), curriculum.getTelefono(), curriculum.getEmail());
    }
}
