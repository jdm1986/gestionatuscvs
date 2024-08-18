package com.example.gestion_curriculums0.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-connection")
    public String checkConnection() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "Connection successful!";
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
