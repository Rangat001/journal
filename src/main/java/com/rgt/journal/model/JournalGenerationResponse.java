package com.rgt.journal.model;

import lombok.Data;

@Data
public class JournalGenerationResponse {
    private String generatedContent;

    public JournalGenerationResponse(String generatedContent) {
        this.generatedContent = generatedContent;
    }

    // Getter
}
