// Este controlador gestiona las acciones del administrador para ver los registros de usuarios.

package com.example.gestion_curriculums0.controller;

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

// Indico que este controlador está asociado a la ruta "/admin" y que acepto solicitudes cross-origin desde localhost
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8000")
public class AdminController {

    // Inyecto el repositorio de logs de usuario para poder acceder a los registros
    @Autowired
    private UserLogRepository userLogRepository;

    // Defino un endpoint protegido para ver los registros de usuarios, que solo puede ser accedido por administradores
    @GetMapping("/registro-usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserLog>> viewUserRegistrations() {
        // Obtengo todos los logs de usuario y los devuelvo en la respuesta
        List<UserLog> logs = userLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}
