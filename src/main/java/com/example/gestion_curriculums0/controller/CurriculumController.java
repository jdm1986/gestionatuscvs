// CurriculumController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

import com.example.gestion_curriculums0.model.*;
import com.example.gestion_curriculums0.repository.*;
import com.example.gestion_curriculums0.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/* Este controlador maneja la funcionalidad relacionada con la gestión de currículums, incluyendo la subida,
descarga, actualización, eliminación y búsqueda de currículums en la base de datos. Permite tanto a usuarios
normales como a administradores acceder a los currículums según sus permisos. Además, ofrece una integración
con un servicio de OCR para extraer texto de archivos PDF, facilitando la búsqueda por palabras clave.
El controlador también proporciona métodos para generar enlaces de subida de currículums, permitiendo a los
usuarios externos enviar sus currículums directamente al sistema de forma segura. */

@RestController
@RequestMapping("/curriculums")
@CrossOrigin(origins = "http://localhost:8000")

public class CurriculumController {

    // Inyecto los servicios y repositorios necesarios para la gestión de curriculums
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
    @Autowired
    private EmailService emailService;

    // Configuro un endpoint para devolver una lista de curriculums en formato DTO
    @ApiOperation(value = "Ver una lista de curriculums disponibles", response = List.class)
    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getAllCurriculums() {
        return curriculumService.getAllCurriculums().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Configuro un endpoint para obtener un curriculum específico por su ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public CurriculumDTO getCurriculumById(@PathVariable Long id) {
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));
        return convertToDTO(curriculum);
    }

    // Configuro un endpoint para obtener los curriculums asociados al usuario autenticado
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

    // Configuro un endpoint para obtener los curriculums de un usuario específico por su ID
    @GetMapping("/usuario/{usuarioId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> getCurriculumsByUsuarioId(@PathVariable Long usuarioId) {
        return curriculumService.getCurriculumsByUsuarioId(usuarioId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Configuro un endpoint para buscar curriculums por nombre
    @GetMapping("/buscar/nombre")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorNombre(@RequestParam String nombre) {
        return curriculumService.buscarPorNombre(nombre).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Configuro un endpoint para buscar curriculums por apellido
    @GetMapping("/buscar/apellido")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorApellido(@RequestParam String apellido) {
        return curriculumService.buscarPorApellido(apellido).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Configuro un endpoint para buscar curriculums por palabra clave y usuario autenticado
    @GetMapping("/buscar/clave")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<CurriculumDTO> buscarPorClave(@RequestParam String clave, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return curriculumService.buscarPorClaveYUsuario(clave, usuario.getId()).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Configuro un endpoint para subir un curriculum en formato PDF y guardar el departamento
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
        // Verifico que el archivo sea un PDF
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

        // Busco el usuario que sube el curriculum
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Convierto el archivo subido a un archivo temporal
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error convirtiendo el archivo", e);
        }

        // Extraigo el texto del PDF usando un servicio
        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(convFile);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
        }

        // Creo un objeto Curriculum con los datos extraídos y el usuario relacionado
        Curriculum curriculum = new Curriculum();
        curriculum.setNombre(nombre);
        curriculum.setApellido(apellido);
        curriculum.setPdfPath(convFile.getPath());
        curriculum.setCvBruto(extractedText);
        curriculum.setSexo(sexo);
        curriculum.setTelefono(telefono);
        curriculum.setEmail(email);
        curriculum.setDepartamento(departamento);
        curriculum.setUsuario(usuario);

        // Guardo el curriculum y retorno una respuesta exitosa con el DTO del curriculum guardado
        return ResponseEntity.ok(convertToDTO(curriculumService.saveCurriculum(curriculum)));
    }

    // Configuro un endpoint para actualizar un curriculum existente
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CurriculumDTO updateCurriculum(@PathVariable Long id,
                                          @RequestBody CurriculumDTO curriculumDTO) {
        // Busco el curriculum por su ID
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        // Actualizo los datos del curriculum
        curriculum.setNombre(curriculumDTO.getNombre());
        curriculum.setApellido(curriculumDTO.getApellido());
        curriculum.setSexo(curriculumDTO.getSexo());
        curriculum.setTelefono(curriculumDTO.getTelefono());
        curriculum.setEmail(curriculumDTO.getEmail());
        curriculum.setDepartamento(curriculumDTO.getDepartamento());

        // Guardo el curriculum actualizado y retorno el DTO
        return convertToDTO(curriculumService.saveCurriculum(curriculum));
    }

    // Configuro un endpoint para actualizar el departamento de un curriculum específico
    @PutMapping("/{idCurriculum}/departamento")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizarDepartamento(
            @PathVariable Long idCurriculum,
            @RequestBody Map<String, String> request) {

        // Verifico si el curriculum existe
        Curriculum curriculum = curriculumService.getCurriculumById(idCurriculum)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        // Obtengo el nuevo departamento desde el request
        String nuevoDepartamento = request.get("departamento");

        if (nuevoDepartamento == null || nuevoDepartamento.isEmpty()) {
            return ResponseEntity.badRequest().body("El departamento no puede estar vacío");
        }

        // Actualizo el departamento
        curriculum.setDepartamento(nuevoDepartamento);

        // Guardo el curriculum actualizado
        curriculumService.saveCurriculum(curriculum);

        return ResponseEntity.ok("Departamento actualizado exitosamente");
    }

    // Configuro un endpoint para eliminar un curriculum por su ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCurriculum(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.ok().build();
    }

    // Configuro un endpoint para descargar el archivo PDF de un curriculum por su ID
    @GetMapping("/pdf/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id) {
        // Busco el curriculum por su ID
        Curriculum curriculum = curriculumService.getCurriculumById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum no encontrado"));

        // Cargo el archivo asociado al curriculum
        File file = new File(curriculum.getPdfPath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        // Defino el tipo de archivo según su extensión
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.getName().endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (file.getName().endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        // Devuelvo el archivo como un recurso descargable
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }

    // Método auxiliar para convertir un Curriculum en DTO
    private CurriculumDTO convertToDTO(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(), curriculum.getNombre(), curriculum.getApellido(), curriculum.getPdfPath(),
                curriculum.getCvBruto(), curriculum.getSexo(), curriculum.getTelefono(), curriculum.getEmail(), curriculum.getDepartamento(), curriculum.getFechaInsercion());
    }

    // Configuro un endpoint para generar un enlace único para que un usuario pueda subir su curriculum
    @PostMapping("/generate-upload-link")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> generateUploadLink(Principal principal) {
        // Busco el usuario que genera el enlace
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Genero un token único
        String token = UUID.randomUUID().toString();

        // Creo un enlace de subida con caducidad de 5 días
        UploadLink uploadLink = new UploadLink();
        uploadLink.setToken(token);
        uploadLink.setExpirationDate(LocalDateTime.now().plusDays(5));
        uploadLink.setUsuario(usuario);

        // Guardo el enlace en la base de datos
        uploadLinkRepository.save(uploadLink);

        // Devuelvo el enlace generado con el token y el userId
        String link = "https://gestionatuscv.es/upload.html?token=" + token + "&userId=" + usuario.getId();
        return ResponseEntity.ok(link);
    }

    // Configuro un endpoint para subir un curriculum usando un token de acceso
    @PostMapping("/upload_with_token")
    public ResponseEntity<?> uploadCurriculumWithToken(
            @RequestParam("token") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String sexo,
            @RequestParam String departamento,
            @RequestParam String telefono,
            @RequestParam String email) {

        // Verifico que el archivo sea un PDF
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos PDF.");
        }

        // Valido el token proporcionado
        UploadLink uploadLink = uploadLinkRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Enlace de subida no válido o ha expirado"));

        // Verifico si el enlace ha expirado
        if (uploadLink.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("El enlace ha expirado");
        }

        // Convierto el archivo subido a un archivo temporal
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error convirtiendo el archivo", e);
        }

        // Extraigo el texto del PDF usando un servicio
        String extractedText;
        try {
            extractedText = pdfService.extractTextFromPdf(convFile);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando el archivo", e);
        }

        // Creo un objeto Curriculum con los datos extraídos y el usuario relacionado
        Curriculum curriculum = new Curriculum();
        curriculum.setNombre(nombre);
        curriculum.setApellido(apellido);
        curriculum.setPdfPath(convFile.getPath());
        curriculum.setCvBruto(extractedText);
        curriculum.setSexo(sexo);
        curriculum.setTelefono(telefono);
        curriculum.setEmail(email);
        curriculum.setDepartamento(departamento);
        curriculum.setUsuario(uploadLink.getUsuario());

        // Guardo el curriculum en la base de datos
        curriculumService.saveCurriculum(curriculum);

        // Envío un correo de notificación al usuario (quien generó el enlace)
        String subjectUsuario = "Nuevo C.V. subido a su cuenta";
        String messageUsuario = "Hola " + uploadLink.getUsuario().getNombreUsuario() + ",\n\n" +
                "Un nuevo C.V. ha sido subido por " + nombre + " " + apellido + ".\n\n" +
                "Gracias por utilizar nuestra plataforma.\n\n" +
                "Saludos cordiales,\nEl equipo de GestionaTusCVs";
        emailService.sendSimpleEmail(uploadLink.getUsuario().getEmail(), subjectUsuario, messageUsuario);

        // Envío un correo de confirmación al aspirante
        String subject = "Confirmación de Envío de C.V.";
        String message = "Estimado/a " + nombre + ",\n\n" +
                "Su C.V. ha sido enviado correctamente a " + uploadLink.getUsuario().getNombreUsuario() + ".\n\n" +
                "Gracias por su tiempo.\n\n" +
                "Saludos cordiales,\nEl equipo de GestionaTusCVs";
        emailService.sendSimpleEmail(email, subject, message);

        // Actualizo el contador de subidas y elimino el enlace si se alcanza el límite
        uploadLink.setUploadCount(uploadLink.getUploadCount() + 1);
        uploadLinkRepository.save(uploadLink);

        if (uploadLink.getUploadCount() >= uploadLink.getUploadLimit()) {
            uploadLinkRepository.delete(uploadLink);
        }

        // Devuelvo una respuesta exitosa
        return ResponseEntity.ok("Currículum subido exitosamente");
    }
}
