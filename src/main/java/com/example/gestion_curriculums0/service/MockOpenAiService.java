package com.example.gestion_curriculums0.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class MockOpenAiService {

    public String getCvSummary(String cvText) {
        // Devuelve un resumen simulado
        return "Este es un resumen simulado del CV.";
    }
}
