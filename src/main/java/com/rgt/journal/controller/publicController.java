package com.rgt.journal.controller;

import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.UserDetailsServiceImpl;
import com.rgt.journal.service.UserService;
import com.rgt.journal.utils.JWTutil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class publicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JWTutil jwTutil;

    @GetMapping("health-check")
    public  String healthCheck() {
        return  "Ok";
    }

    // Signup(Create new user)
    @PostMapping("/signup")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity userEntity) {
        try {
            userService.saveNewUser(userEntity);
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity userEntity) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userEntity.getUsername(),userEntity.getPassword()));
            UserDetails user = userDetailsService.loadUserByUsername(userEntity.getUsername());
            String token = jwTutil.generateToken(user.getUsername());
            return new ResponseEntity<>(token,HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return  new ResponseEntity<>("Incorrect Credential",HttpStatus.BAD_REQUEST);
        }
    }
}
