package com.rgt.journal.service;

import com.rgt.journal.Repository.UserRepository;
import com.rgt.journal.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    //                                         Save New Entry
    public void saveUser(UserEntity userEntity) {
        if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty() || userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        userRepository.save(userEntity);
    }

    public boolean saveNewUser(UserEntity userEntity){
        try {
            if (userEntity.getUsername() == null || userEntity.getUsername().isEmpty() || userEntity.getPassword() == null || userEntity.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntity.setRoles(Arrays.asList("USER"));
            userRepository.save(userEntity);
            return  true;
        }catch (Exception e){
            log.error(" xbcgn fhm fnfsn");
            return false;
        }
    }


    //                                             Find By UserName
    public UserEntity findByUserName(String username){
        return userRepository.findByusername(username);
    }

    //                                            Get All Entries
    public List<UserEntity> getAllUser(){
        return userRepository.findAll();
    }

    //                                        Delete Journal By id
    public void deleteUser(ObjectId id){
        userRepository.deleteById(id);
    }

    //                                          Update Journal
    public UserEntity updateUser(ObjectId id, UserEntity Updated){
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user != null){
            user.setUsername(Updated.getUsername());
            user.setPassword(Updated.getPassword());
            userRepository.save(user);
        }
        return user;
    }


}
