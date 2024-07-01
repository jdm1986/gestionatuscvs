package com.example.gestion_curriculums0;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomOpenAiService {

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

        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }
}
