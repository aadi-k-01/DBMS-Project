package com.example.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_preferences")
public class UserPreference {
    @Id
    private String id;
    private String userId;
    private String preferredTimeOfDay;
    private boolean emailNotificationsEnabled;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPreferredTimeOfDay() { return preferredTimeOfDay; }
    public void setPreferredTimeOfDay(String preferredTimeOfDay) { this.preferredTimeOfDay = preferredTimeOfDay; }
    
    public boolean isEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) { this.emailNotificationsEnabled = emailNotificationsEnabled; }
}
