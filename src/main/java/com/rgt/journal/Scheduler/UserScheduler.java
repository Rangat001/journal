package com.rgt.journal.Scheduler;

import com.rgt.journal.Repository.UserRepositoryImpl;
import com.rgt.journal.apiResponse.cache.AppCache;
import com.rgt.journal.entity.JournalEntity;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.enums.Sentiment;
import com.rgt.journal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserScheduler {

    @Autowired
    private EmailService emailService ;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Scheduled(cron =  "0 0 9 * * SUN")
    public void fetchUserAndSendSaMail(){
        List<UserEntity> users = userRepository.getUserForSA();
        for (UserEntity userEntity : users){
            List<JournalEntity> journalEntries = userEntity.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentcounts = new HashMap<>();
            for (Sentiment sentiment : sentiments){
                if(sentiment != null){
                     sentimentcounts.put(sentiment,sentimentcounts.getOrDefault(sentiment,0)+1);
                }
            }
            Sentiment mostFrequentSentimet = null;
            int maxCounts = 0;
            for(Map.Entry<Sentiment, Integer> entry : sentimentcounts.entrySet()){
                if(entry.getValue() > maxCounts){
                    maxCounts  = entry.getValue();
                    mostFrequentSentimet = entry.getKey();
                }
            }
            if(mostFrequentSentimet != null){
                emailService.sendEmail(userEntity.getEmail(),"Sentiment for last 7 days ",mostFrequentSentimet.toString());

            }

        }
    }

    @Scheduled(cron =  "0 0/10 * ? * *")
    public  void appcache(){
        appCache.init();
    }

}
