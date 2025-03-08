package com.rgt.journal.controller;

import com.rgt.journal.apiResponse.QuotesResponse;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.QuotesService;
import com.rgt.journal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    

    @Autowired
    private QuotesService quotesService;

    @GetMapping()
    public ResponseEntity<UserEntity> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return new ResponseEntity<>(userService.findByUserName(username),HttpStatus.FOUND);
    }



    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody UserEntity userEntity){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findByUserName(username);
//        System.out.println("Update User Called by "+username);
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        userService.saveNewUser(user);                                 // NewUser() Because If Password changed then it need to encrypt
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.deleteUser(userService.findByUserName(username).getId());
//        System.out.println("Delete User Called by" + username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("index")
    public ResponseEntity<?> greeeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        QuotesResponse qoute =  quotesService.getQuote();

        String greet = "Hello "+authentication.getName()+" \nToday's Quote: "+qoute.getQuote();

        return new ResponseEntity<>(greet,HttpStatus.OK);
    }
}
