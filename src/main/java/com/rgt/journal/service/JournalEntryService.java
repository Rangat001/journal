package com.rgt.journal.service;

import com.rgt.journal.Repository.JournalRepository;
import com.rgt.journal.entity.JournalEntity;
import com.rgt.journal.entity.UserEntity;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalEntryService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserService userService;


    //                                         Save New Entry
//    @Transactional                                                                      uncomment when Use MongoAtlas Server
    public void  saveEntry(JournalEntity journalEntity, String username){
        UserEntity user = userService.findByUserName(username);
        journalEntity.setDate(LocalDateTime.now());
        JournalEntity saved = journalRepository.save(journalEntity);
        try {
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            // Rollback or compensate for the saved journalEntity if necessary
            journalRepository.delete(saved);
            throw e;
        }
    }

    //                                             Find By ID
    public JournalEntity getEntryById(ObjectId id){
        return journalRepository.findById(id).orElse(null);
    }

    //                                            Get All Entries
    public List<JournalEntity> getAllEntries(){
        return journalRepository.findAll();
    }

    //                                        Delete Journal By id
    public void deleteEntry(ObjectId id, String username){
        UserEntity user = userService.findByUserName(username);
        user.getJournalEntries().removeIf(x ->x.getId().equals(id));
        userService.saveUser(user);
        journalRepository.deleteById(id);
    }

    //                                          Update Journal
    public JournalEntity updateEntry(ObjectId id, JournalEntity journalEntity){
        JournalEntity journal = journalRepository.findById(id).orElse(null);
        if (journal != null){
            journal.setTitle(journalEntity.getTitle());
            journal.setContent(journalEntity.getContent());
            journalRepository.save(journal);
        }
        return journal;
    }
}
