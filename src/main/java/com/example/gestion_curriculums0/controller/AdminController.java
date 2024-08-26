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

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8000")
public class AdminController {

    @Autowired
    private UserLogRepository userLogRepository;

    // Endpoint protegido para ver los registros de usuarios
    @GetMapping("/registro-usuarios")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserLog>> viewUserRegistrations() {
        List<UserLog> logs = userLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    // Otros endpoints de tu AdminController permanecen sin cambios
}
