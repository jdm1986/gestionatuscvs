package com.example.gestion_curriculums0.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/check-connection")
    public String checkConnection() {
        try {
            List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
            return "Connected! Tables in the database: " + tables;
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}
