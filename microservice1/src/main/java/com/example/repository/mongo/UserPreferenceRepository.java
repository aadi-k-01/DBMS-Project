package com.example.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.mongo.UserPreference;

@Repository
public interface UserPreferenceRepository extends MongoRepository<UserPreference, String> {
}
