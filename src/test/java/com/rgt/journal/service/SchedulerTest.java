package com.rgt.journal.service;

import com.rgt.journal.Scheduler.UserScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

@SpringBootTest
public class SchedulerTest {
    @Autowired
    private UserScheduler userScheduler;

    @Test
    public void testScheduler(){
        userScheduler.fetchUserAndSendSaMail();
    }
}
