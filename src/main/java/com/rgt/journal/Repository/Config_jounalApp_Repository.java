package com.rgt.journal.Repository;

import com.rgt.journal.entity.ConfigJournalAppEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Config_jounalApp_Repository extends MongoRepository<ConfigJournalAppEntity, ObjectId> {

}
