package com.example.gestion_curriculums0;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev") // Puedes usar un perfil específico para el desarrollo
public class CustomOpenAiServiceSimulado {

    public String getCvSummary(String cvText) {
        // Retornar una respuesta simulada
        return "Este es un resumen simulado del CV.";
    }
}
