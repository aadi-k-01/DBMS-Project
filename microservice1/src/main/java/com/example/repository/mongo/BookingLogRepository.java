package com.example.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.mongo.BookingLog;

@Repository
public interface BookingLogRepository extends MongoRepository<BookingLog, String> {
}
