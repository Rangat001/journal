package com.rgt.journal.Repository;

import com.rgt.journal.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity,ObjectId> {
    UserEntity findByusername(String username);
}
