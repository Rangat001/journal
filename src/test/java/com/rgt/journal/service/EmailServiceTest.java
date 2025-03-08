package com.rgt.journal.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {
    @Autowired EmailService emailService;

    @Test
    void testSendmail(){
        emailService.sendEmail("rangatprajapati@gmail.com","testing Function","Hi this is the sample mail");
    }
}
