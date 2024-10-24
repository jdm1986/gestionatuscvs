// AdminController.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.controller;

/* El UsuarioController se encarga de gestionar las operaciones relacionadas con
la administración de usuarios, como el registro, consulta y eliminación de usuarios en la
base de datos. Proporciona endpoints para que los usuarios puedan registrarse, consultar su
información y modificar sus datos personales. Además, permite a los administradores listar todos
los usuarios registrados y eliminar usuarios específicos. Los métodos en esta clase están diseñados
para protegerse adecuadamente según los permisos del usuario, garantizando que solo los usuarios con
las credenciales adecuadas puedan ejecutar ciertas operaciones. */

import com.example.gestion_curriculums0.model.UserLog;
import com.example.gestion_curriculums0.repository.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Con la anotación @RestController, indico que este controlador está asociado a la ruta "/admin"
// y acepto solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8000")
public class AdminController {

    // Inyecto el repositorio de logs de usuario para acceder a los registros de usuarios
    @Autowired
    private UserLogRepository userLogRepository;

    // Configuro un endpoint protegido para ver los registros de usuarios, accesible solo para administradores
    @GetMapping("/registro-usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserLog>> viewUserRegistrations() {
        // Obtengo todos los logs de usuario y los devuelvo en la respuesta HTTP
        List<UserLog> logs = userLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}
