package com.rgt.journal.Repository;

import com.rgt.journal.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<UserEntity> getUserForSA(){
        Query query = new Query();

//        query.addCriteria(Criteria.where("email").exists(true));
//        query.addCriteria(Criteria.where("email").ne(null).ne(""));
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
        List<UserEntity> userEntities = mongoTemplate.find(query, UserEntity.class);
        return userEntities;
    }
}
