package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.UploadLink;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UploadLinkRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.CurriculumService;
import com.example.gestion_curriculums0.service.PdfService;
import com.example.gestion_curriculums0.service.OcrService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @Autowired
    private OcrService ocrService;

    @Autowired
    private UploadLinkRepository uploadLinkRepository;

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
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
        return convertToDTO(curriculum);
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
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> uploadCurriculum(@RequestParam("file") MultipartFile file,
                                              @RequestParam Long userId,
                                              @RequestParam String nombre,
                                              @RequestParam String apellido,
                                              @RequestParam String sexo,
                                              @RequestParam String telefono,
                                              @RequestParam String email) {
        // Validar que el archivo sea un PDF
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

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
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
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

        return ResponseEntity.ok(convertToDTO(curriculumService.saveCurriculum(curriculum)));
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
                if (file.getContentType().equals("application/pdf")) {
                    extractedText = pdfService.extractTextFromPdf(convFile);
                } else {
                    extractedText = ocrService.extractTextFromImage(convFile);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error extrayendo texto del archivo", e);
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
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id) {
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
        File file = new File(curriculum.getPdfPath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (file.getName().endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (file.getName().endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }

    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(), curriculum.getNombre(), curriculum.getApellido(), curriculum.getPdfPath(),
                curriculum.getResumenCv(), curriculum.getSexo(), curriculum.getTelefono(), curriculum.getEmail(), curriculum.getFechaInsercion());
    }

    @PostMapping("/generate-upload-link")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> generateUploadLink(Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString(); // Generar un token único

        UploadLink uploadLink = new UploadLink();
        uploadLink.setToken(token);
        uploadLink.setExpirationDate(LocalDateTime.now().plusDays(5)); // Caducidad de 5 días
        uploadLink.setUsuario(usuario); // Asignar el usuario que genera el enlace

        uploadLinkRepository.save(uploadLink);

        String link = "https://gestionatuscv.es/upload.html?token=" + token;
        return ResponseEntity.ok(link);
    }

    @PostMapping("/upload_with_token")
    public ResponseEntity<?> uploadCurriculumWithToken(
            @RequestParam("token") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String sexo,
            @RequestParam String telefono,
            @RequestParam String email) {

        // Verificar que el archivo sea un PDF
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

        // Validar el token
        UploadLink uploadLink = uploadLinkRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Enlace de subida no válido o ha expirado"));

        if (uploadLink.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("El enlace ha expirado");
        }

        // Procesar la subida
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error convirtiendo el archivo", e);
        }

        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(convFile);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
        }

        // Crear y guardar el curriculum asociado al usuario del enlace
        Curriculum curriculum = new Curriculum();
        curriculum.setNombre(nombre);
        curriculum.setApellido(apellido);
        curriculum.setPdfPath(convFile.getPath());
        curriculum.setCvBruto(extractedText);
        curriculum.setSexo(sexo);
        curriculum.setTelefono(telefono);
        curriculum.setEmail(email);
        curriculum.setUsuario(uploadLink.getUsuario()); // Asociar el CV al usuario del enlace

        curriculumService.saveCurriculum(curriculum);

        // Actualizar el contador de subidas
        uploadLink.setUploadCount(uploadLink.getUploadCount() + 1);
        uploadLinkRepository.save(uploadLink);

        if (uploadLink.getUploadCount() >= uploadLink.getUploadLimit()) {
            uploadLinkRepository.delete(uploadLink); // Eliminar el link si ha alcanzado su límite
        }

        return ResponseEntity.ok("Currículum subido exitosamente");
    }
}
