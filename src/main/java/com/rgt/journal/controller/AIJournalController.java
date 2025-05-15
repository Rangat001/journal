package com.rgt.journal.controller;

import com.rgt.journal.model.JournalGenerationRequest;
import com.rgt.journal.model.JournalImprovementRequest;
import com.rgt.journal.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai")
public class AIJournalController {

    @Autowired
    private AIService aiService;

    // Endpoint for content improvement
    @PostMapping("/improve")
    public ResponseEntity<?> improveContent(@RequestBody JournalImprovementRequest request) {
        try {
            String improvedContent = aiService.improveContent(request.getTitle(), request.getMood(), request.getContent());
            return new ResponseEntity<>(improvedContent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error improving content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint for content generation
    @PostMapping("/generate")
    public ResponseEntity<?> generateContent(@RequestBody JournalGenerationRequest request) {
        try {
            System.out.println("Generate called");
            String generatedContent = aiService.generateContent(request.getTitle(), request.getMood());

            return new ResponseEntity<>(generatedContent, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

