package com.rgt.journal.service;

import com.rgt.journal.Repository.UserRepository;
import com.rgt.journal.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

//@SpringBootTest

@Disabled
public class UserDetailsServiceImplTest {
//    @Autowired
    @InjectMocks                                                // Use Mock to not use original Resources that's why SpringBOotTest Is disable
    private UserDetailsServiceImpl userDetailsService;

//    @MockitoBean                                             MockitoBean Use When we use SpringBootTest if use InjectMock then use Mock
    @Mock
    private UserRepository userRepository;
    @Disabled
    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);          // To intial All Mock resource 
    }

    @Disabled
    @Test
    void loadUserByUsernameTest(){
        UserEntity mockUserEntity = new UserEntity("ram","dsubsdjhfd");
        mockUserEntity.setRoles(List.of("USER")); // Set roles as a List of Strings

        // Mock the repository call to return the mock UserEntity
        when(userRepository.findByusername(ArgumentMatchers.anyString())).thenReturn(mockUserEntity);

        // Call the service method
        UserDetails userDetails = userDetailsService.loadUserByUsername("ram");

        // Assertions
        Assertions.assertNotNull(userDetails, "UserDetails should not be null");
    }
}
