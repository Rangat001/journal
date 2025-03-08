package com.rgt.journal.apiResponse.cache;

import com.rgt.journal.Repository.Config_jounalApp_Repository;
import com.rgt.journal.entity.ConfigJournalAppEntity;
import jakarta.annotation.PostConstruct;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum  keys{
        QOUTE_API;
    }
    @Autowired
    private Config_jounalApp_Repository configJounalAppRepository;

    public Map<String,String> AppCache;

    @PostConstruct
    public void init(){
        AppCache = new HashMap<>();
        List<ConfigJournalAppEntity> all =  configJounalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity:all) {
            AppCache.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue());
        }
    }
}
