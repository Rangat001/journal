package com.rgt.journal.model;

import lombok.Data;

@Data
public class JournalImprovementResponse {
    private String improvedContent;

    public JournalImprovementResponse(String improvedContent) {
        this.improvedContent = improvedContent;
    }

    // Getter
}
