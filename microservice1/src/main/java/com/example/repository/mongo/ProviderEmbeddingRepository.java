package com.example.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.mongo.ProviderEmbedding;

@Repository
public interface ProviderEmbeddingRepository extends MongoRepository<ProviderEmbedding, String> {
}
