package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.CurriculumService;
import com.example.gestion_curriculums0.service.PdfService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/curriculums")
@CrossOrigin(origins = "http://localhost:8000")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PdfService pdfService;

    @ApiOperation(value = "Ver una lista de curriculums disponibles", response = List.class)
    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getAllCurriculums() {
        return curriculumService.getAllCurriculums().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public CurriculumDTO getCurriculumById(@PathVariable Long id) {
        return curriculumService.getCurriculumById(id).map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
    }

    @GetMapping("/usuario")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getCurriculumsByCurrentUser(Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return curriculumService.getCurriculumsByUsuarioId(usuario.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumService.getCurriculumsByUsuarioId(usuarioId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/nombre")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorNombre(@RequestParam String nombre) {
        return curriculumService.buscarPorNombre(nombre).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/apellido")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorApellido(@RequestParam String apellido) {
        return curriculumService.buscarPorApellido(apellido).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/clave")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorClave(@RequestParam String clave, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return curriculumService.buscarPorClaveYUsuario(clave, usuario.getId()).stream().map(this::convertToDTO).collect(Collectors.toList());
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error convirtiendo el archivo", e);
        }

        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(convFile);
        } catch (IOException e) {
            throw new RuntimeException("Error extrayendo texto del PDF", e);
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

        return convertToDTO(curriculumService.saveCurriculum(curriculum));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CurriculumDTO updateCurriculum(@PathVariable Long id,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam @Valid String nombre,
                                          @RequestParam @Valid String apellido,
                                          @RequestParam @Valid String sexo,
                                          @RequestParam @Valid String telefono,
                                          @RequestParam @Valid String email) {
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        if (file != null && !file.isEmpty()) {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error convirtiendo el archivo", e);
            }

            String extractedText;
            try {
                extractedText = pdfService.extractTextFromPdf(convFile);
            } catch (IOException e) {
                throw new RuntimeException("Error extrayendo texto del PDF", e);
            }

            curriculum.setPdfPath(convFile.getPath());
            curriculum.setCvBruto(extractedText);
        }

        curriculum.setNombre(nombre);
        curriculum.setApellido(apellido);
        curriculum.setSexo(sexo);
        curriculum.setTelefono(telefono);
        curriculum.setEmail(email);

        return convertToDTO(curriculumService.saveCurriculum(curriculum));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCurriculum(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pdf/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Resource> descargarPdf(@PathVariable Long id) {
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
        File file = new File(curriculum.getPdfPath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(), curriculum.getNombre(), curriculum.getApellido(), curriculum.getPdfPath(),
                curriculum.getResumenCv(), curriculum.getSexo(), curriculum.getTelefono(), curriculum.getEmail(), curriculum.getFechaInsercion());
    }
}
