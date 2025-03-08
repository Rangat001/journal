package com.rgt.journal.repository;


import com.rgt.journal.Repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryImpltest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    public void testGetUserforSA(){
        userRepository.getUserForSA();
    }
}
