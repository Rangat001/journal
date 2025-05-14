package com.rgt.journal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.token}")
    private String apiToken;

    // Method for content improvement
    public String improveContent(String title, String mood, String content) {
        String context = "Improve the journal entry with the title '" + title +
                "' and mood '" + mood + "':\n\n" + content;
        return callAI(context, title, mood);
    }

    // Method for content generation
    public String generateContent(String title, String mood) {
        String context = "Generate a journal entry with the title '" + title +
                "' and mood '" + mood + "' in 100 to 400 words.";
        return callAI(context, title, mood);
    }

    // Helper method to call the API
    private String callAI(String context, String title, String mood) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiToken);

        // Set body
        String requestBody = "{"
                + "\"context\": \"" + context + "\","
                + "\"formality\": \"default\","
                + "\"keywords\": [\"" + title + "\"],"
                + "\"max_tokens\": 2048,"
                + "\"model\": \"gpt-4o\","
                + "\"n\": 1,"
                + "\"source_lang\": \"en\","
                + "\"target_lang\": \"en\","
                + "\"temperature\": null,"
                + "\"title\": \"" + title + "\""
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Call API
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        // Return the response body
        return response.getBody();
    }
}