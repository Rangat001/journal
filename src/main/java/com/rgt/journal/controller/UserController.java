package com.rgt.journal.controller;

import com.rgt.journal.apiResponse.QuotesResponse;
import com.rgt.journal.config.RedisConfig;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.QuotesService;
import com.rgt.journal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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
        user.setEmail(userEntity.getEmail());
        user.setSentimentAnalysis(userEntity.getSentimentAnalysis());
        if (userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
            user.setPassword(user.getPassword());
            userService.saveUser(user);
        }else{
            user.setPassword(userEntity.getPassword());
            userService.saveNewUser(user);
        }
                                       // NewUser() Because If Password changed then it need to encrypt
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

    @GetMapping("/index")
    public ResponseEntity<?> greeeting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Redis operations to check and get cached quote
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        String redisKey = "quote:" + authentication.getName();
        String cachedQuote = (String) valueOps.get(redisKey);

        if (cachedQuote != null) {
            return new ResponseEntity<>(authentication.getName() + "\n" + cachedQuote, HttpStatus.OK);
        }

        QuotesResponse qoute =  quotesService.getQuote();

        // set quote in cache
        valueOps.set(redisKey, qoute.getQuote(), Duration.ofHours(24));

        String greet = authentication.getName()+" \n"+qoute.getQuote();

        return new ResponseEntity<>(greet,HttpStatus.OK);
    }
}
