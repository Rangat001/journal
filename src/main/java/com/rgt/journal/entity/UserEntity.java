package com.rgt.journal.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "users")
public class UserEntity {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @NonNull
    private String username;
    private String email;

    private boolean sentimentAnalysis;
    @NonNull
    private String password;
    @DBRef
    private  List<JournalEntity> journalEntries = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public boolean getSentimentAnalysis(){
        return this.sentimentAnalysis;
    }
    public void setSentimentAnalysis(boolean b){
        this.sentimentAnalysis = b;
    }
}
