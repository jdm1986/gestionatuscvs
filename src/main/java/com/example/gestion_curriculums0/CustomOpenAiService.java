package com.example.gestion_curriculums0;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.HttpException;

@Service
public class CustomOpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOpenAiService.class);

    private final OpenAiService openAiService;

    public CustomOpenAiService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public String getCvSummary(String cvText) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Resume this CV: " + cvText)
                .model("text-davinci-003")
                .maxTokens(150)
                .build();

        try {
            logger.info("Sending request to OpenAI API");
            CompletionResult result = openAiService.createCompletion(completionRequest);
            logger.info("Received response from OpenAI API");
            return result.getChoices().get(0).getText().trim();
        } catch (HttpException e) {
            // Manejo del error HTTP 404 u otros errores HTTP
            logger.error("HTTP Error: " + e.code() + " - " + e.message(), e);
            if (e.code() == 404) {
                logger.error("Error 404: Endpoint no encontrado");
            }
            throw e; // Relanzar la excepción si es necesario
        } catch (Exception e) {
            // Manejo de otras excepciones
            logger.error("Error al obtener el resumen del CV", e);
            throw e;
        }
    }
}
