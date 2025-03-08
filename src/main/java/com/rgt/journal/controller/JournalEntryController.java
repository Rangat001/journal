package com.rgt.journal.controller;

import com.rgt.journal.entity.JournalEntity;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.JournalEntryService;
import com.rgt.journal.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<?> getAllJournalOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findByUserName(username);
        List<JournalEntity> All = user.getJournalEntries();
        if (All != null && !All.isEmpty()){
//            System.out.println("Get Call for review all Journal of : " + username);
            return new ResponseEntity<>(All, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping()
    public ResponseEntity<JournalEntity> createEntry(@RequestBody JournalEntity journalEntity){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            journalEntryService.saveEntry(journalEntity,username);
//            System.out.println("Received New JournalEntity: " + journalEntity);
            if(journalEntity.getSentiment() == null){
                journalEntity.setSentiment(null);
            }
            return new ResponseEntity<>(journalEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("{id}")
    public ResponseEntity<?> getEntryById(@PathVariable ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user  = userService.findByUserName(username);
        List<JournalEntity> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            JournalEntity journalEntity = journalEntryService.getEntryById(id);
            if (journalEntity != null) {
//                System.out.println("Get Journal Called for "+ id);
                return new ResponseEntity<>(journalEntryService.getEntryById(id), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<?>  updateEntry(@PathVariable ObjectId id,@RequestBody JournalEntity journalEntity){
        JournalEntity journalEntity1 = journalEntryService.getEntryById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user  = userService.findByUserName(username);
        List<JournalEntity> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            if (journalEntity != null) {
//                System.out.println("Update Journal Called for journal id "+id +"By "+username+"To update" + journalEntity1 +"to : " + journalEntity );
                return new ResponseEntity<>(journalEntryService.updateEntry(id, journalEntity),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable ObjectId id){
        JournalEntity journalEntity1 = journalEntryService.getEntryById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user  = userService.findByUserName(username);
        List<JournalEntity> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            if (journalEntity1 != null) {
//                System.out.println("Delete Journal Called to delete "+id+" by "+username);
                journalEntryService.deleteEntry(id,username);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
