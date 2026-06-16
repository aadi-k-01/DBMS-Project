package com.example.model.mongo;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "provider_embeddings")
public class ProviderEmbedding {
    @Id
    private String id;
    private String providerId;
    private List<Double> embeddingData;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    
    public List<Double> getEmbeddingData() { return embeddingData; }
    public void setEmbeddingData(List<Double> embeddingData) { this.embeddingData = embeddingData; }
}
