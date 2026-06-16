package com.example.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.Users;
import com.example.model.mongo.UserPreference;
import com.example.repository.UserRepository;
import com.example.repository.mongo.UserPreferenceRepository;

@Service
public class UserPreferenceService {

    @Autowired
    UserPreferenceRepository prefRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    JWTService jwtService;

    public Object savePreference(UserPreference pref, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users user = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (user == null) {
                response.put("code", 404);
                response.put("message", "User not found.");
                return response;
            }

            // check if exists
            UserPreference existing = prefRepo.findAll().stream()
                    .filter(p -> user.getId().equals(p.getUserId()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setPreferredTimeOfDay(pref.getPreferredTimeOfDay());
                existing.setEmailNotificationsEnabled(pref.isEmailNotificationsEnabled());
                prefRepo.save(existing);
            } else {
                pref.setUserId(user.getId());
                prefRepo.save(pref);
            }

            response.put("code", 200);
            response.put("message", "Preferences saved successfully.");
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object getPreference(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users user = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (user == null) {
                response.put("code", 404);
                response.put("message", "User not found.");
                return response;
            }

            UserPreference pref = prefRepo.findAll().stream()
                    .filter(p -> user.getId().equals(p.getUserId()))
                    .findFirst()
                    .orElse(null);

            response.put("code", 200);
            response.put("preference", pref);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }
}
