package com.example.gestion_curriculums0;

import org.springframework.stereotype.Service;

@Service
public class CustomOpenAiService {

    public String getCvSummary(String cvText) {
        // Simulando la respuesta de OpenAI
        return "Este es un resumen simulado del CV.";
    }
}
