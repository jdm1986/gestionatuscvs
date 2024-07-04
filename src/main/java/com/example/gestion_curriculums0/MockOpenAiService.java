package com.example.gestion_curriculums0;

import org.springframework.stereotype.Service;

@Service
public class MockOpenAiService {

    public String getCvSummary(String cvText) {
        // Devuelve un resumen simulado
        return "Este es un resumen simulado del CV.";
    }
}
