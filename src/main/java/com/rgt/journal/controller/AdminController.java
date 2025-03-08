package com.rgt.journal.controller;

import com.rgt.journal.Repository.UserRepository;
import com.rgt.journal.apiResponse.cache.AppCache;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppCache appCache;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("all-user")
    public ResponseEntity<?> getAll(){
        List<UserEntity> All = userService.getAllUser();
        if (All != null && !All.isEmpty()){
            return new ResponseEntity<>(All, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("add-admin")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity userEntity) {
        // SaveNewUser Use only for Create user
        try {
            if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty()
                    || userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Admin name or password cannot be null or empty");
            }
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRoles(Arrays.asList("ADMIN","USER"));
            userRepository.save(userEntity);                                   // Save New User With Encrypted PassWord
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("clear-app-cache")
    public void clearAppCache(){
        appCache.init();
    }
}
