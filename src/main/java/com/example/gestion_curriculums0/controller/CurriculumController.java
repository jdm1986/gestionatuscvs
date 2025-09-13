package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.Curriculum;
import com.example.gestion_curriculums0.model.CurriculumDTO;
import com.example.gestion_curriculums0.model.UploadLink;
import com.example.gestion_curriculums0.model.Usuario;
import com.example.gestion_curriculums0.repository.UploadLinkRepository;
import com.example.gestion_curriculums0.repository.UsuarioRepository;
import com.example.gestion_curriculums0.service.ArchivoService;
import com.example.gestion_curriculums0.service.CurriculumService;
import com.example.gestion_curriculums0.service.EmailService;
import com.example.gestion_curriculums0.service.PdfService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/curriculums")
@CrossOrigin(origins = "http://localhost:8000")
public class CurriculumController {

    @Autowired private CurriculumService curriculumService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PdfService pdfService;
    @Autowired private UploadLinkRepository uploadLinkRepository;
    @Autowired private EmailService emailService;
    @Autowired private ArchivoService archivoService;

    @ApiOperation(value = "Ver una lista de curriculums disponibles", response = List.class)
    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getAllCurriculums() {
        return curriculumService.getAllCurriculums()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public CurriculumDTO getCurriculumById(@PathVariable Long id) {
        Curriculum c = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
        return convertToDTO(c);
    }

    @GetMapping("/usuario")
    public ResponseEntity<?> getCurriculumsByCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        String username = userDetails.getUsername();

        Usuario usuario = usuarioRepository.findByNombreUsuario(username).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        List<Curriculum> curriculums = curriculumService.getCurriculumsByUsuarioId(usuario.getId());
        List<CurriculumDTO> dtos = curriculums.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumService.getCurriculumsByUsuarioId(usuarioId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/nombre")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorNombre(@RequestParam String nombre) {
        return curriculumService.buscarPorNombre(nombre)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/apellido")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorApellido(@RequestParam String apellido) {
        return curriculumService.buscarPorApellido(apellido)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/buscar/clave")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorClave(@RequestParam String clave, Principal principal) {
        Usuario u = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return curriculumService.buscarPorClaveYUsuario(clave, u.getId())
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> uploadCurriculum(@RequestParam("file") MultipartFile file,
                                              @RequestParam Long userId,
                                              @RequestParam String nombre,
                                              @RequestParam String apellido,
                                              @RequestParam String sexo,
                                              @RequestParam String departamento,
                                              @RequestParam String telefono,
                                              @RequestParam String email) {

        if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String absolutePath = archivoService.saveFile(file, usuario.getNombreUsuario());

        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(new File(absolutePath));
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
        }

        Curriculum c = new Curriculum();
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setPdfPath(absolutePath);
        c.setCvBruto(extractedText);
        c.setSexo(sexo);
        c.setTelefono(telefono);
        c.setEmail(email);
        c.setDepartamento(departamento);
        c.setUsuario(usuario);

        Curriculum saved = curriculumService.saveCurriculum(c);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CurriculumDTO updateCurriculum(@PathVariable Long id,
                                          @RequestBody CurriculumDTO dto) {
        Curriculum c = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        c.setNombre(dto.getNombre());
        c.setApellido(dto.getApellido());
        c.setSexo(dto.getSexo());
        c.setTelefono(dto.getTelefono());
        c.setEmail(dto.getEmail());
        c.setDepartamento(dto.getDepartamento());

        return convertToDTO(curriculumService.saveCurriculum(c));
    }

    @PutMapping("/{idCurriculum}/departamento")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarDepartamento(@PathVariable Long idCurriculum,
                                                    @RequestBody Map<String, String> request) {
        Curriculum c = curriculumService.getCurriculumById(idCurriculum)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        String nuevoDepartamento = request.get("departamento");
        if (nuevoDepartamento == null || nuevoDepartamento.isEmpty()) {
            return ResponseEntity.badRequest().body("El departamento no puede estar vacío");
        }

        c.setDepartamento(nuevoDepartamento);
        curriculumService.saveCurriculum(c);
        return ResponseEntity.ok("Departamento actualizado exitosamente");
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
        Curriculum c = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        String path = c.getPdfPath();
        Resource resource = archivoService.loadFile(path);

        String filename = Paths.get(path).getFileName().toString();
        MediaType mediaType = filename.toLowerCase().endsWith(".pdf")
                ? MediaType.APPLICATION_PDF
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(resource);
    }

    @PostMapping("/generate-upload-link")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> generateUploadLink(Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        UploadLink link = new UploadLink();
        link.setToken(token);
        link.setExpirationDate(LocalDateTime.now().plusDays(5));
        link.setUsuario(usuario);
        uploadLinkRepository.save(link);

        String url = "https://gestionatuscv.es/upload.html?token=" + token + "&userId=" + usuario.getId();
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload_with_token")
    public ResponseEntity<?> uploadCurriculumWithToken(@RequestParam("token") String token,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam String nombre,
                                                       @RequestParam String apellido,
                                                       @RequestParam String sexo,
                                                       @RequestParam String departamento,
                                                       @RequestParam String telefono,
                                                       @RequestParam String email) {

        if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

        UploadLink uploadLink = uploadLinkRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Enlace de subida no válido o ha expirado"));

        if (uploadLink.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("El enlace ha expirado");
        }

        String absolutePath = archivoService.saveFile(file, uploadLink.getUsuario().getNombreUsuario());

        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(new File(absolutePath));
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
        }

        Curriculum c = new Curriculum();
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setPdfPath(absolutePath);
        c.setCvBruto(extractedText);
        c.setSexo(sexo);
        c.setTelefono(telefono);
        c.setEmail(email);
        c.setDepartamento(departamento);
        c.setUsuario(uploadLink.getUsuario());
        curriculumService.saveCurriculum(c);

        emailService.sendSimpleEmail(uploadLink.getUsuario().getEmail(),
                "Nuevo C.V. subido a su cuenta",
                "Hola " + uploadLink.getUsuario().getNombreUsuario() + ",\n\n" +
                        "Un nuevo C.V. ha sido subido por " + nombre + " " + apellido + ".\n\n" +
                        "Saludos,\nGestionaTusCVs");

        emailService.sendSimpleEmail(email,
                "Confirmación de Envío de C.V.",
                "Estimado/a " + nombre + ",\n\n" +
                        "Su C.V. ha sido enviado correctamente a " + uploadLink.getUsuario().getNombreUsuario() + ".\n\n" +
                        "Saludos,\nGestionaTusCVs");

        uploadLink.setUploadCount(uploadLink.getUploadCount() + 1);
        uploadLinkRepository.save(uploadLink);
        if (uploadLink.getUploadCount() >= uploadLink.getUploadLimit()) {
            uploadLinkRepository.delete(uploadLink);
        }

        return ResponseEntity.ok("Currículum subido exitosamente");
    }

    private CurriculumDTO convertToDTO(Curriculum c) {
        return new CurriculumDTO(
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getPdfPath(),
                c.getCvBruto(),
                c.getSexo(),
                c.getTelefono(),
                c.getEmail(),
                c.getDepartamento(),
                c.getFechaInsercion()
        );
    }
}

