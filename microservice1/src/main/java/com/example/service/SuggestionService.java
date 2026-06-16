package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.TimeSlot;
import com.example.model.Users;
import com.example.model.mongo.UserPreference;
import com.example.repository.TimeSlotRepository;
import com.example.repository.UserRepository;
import com.example.repository.mongo.UserPreferenceRepository;

@Service
public class SuggestionService {

    @Autowired
    TimeSlotRepository slotRepo;

    @Autowired
    UserPreferenceRepository prefRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    JWTService jwtService;

    public Object getSuggestions(String token) {
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

            List<TimeSlot> allAvailable = slotRepo.findByIsAvailableTrue();
            List<TimeSlot> recommendations = new ArrayList<>();

            if (pref == null || pref.getPreferredTimeOfDay() == null) {
                recommendations = allAvailable; // No preference, return all
            } else {
                String preferred = pref.getPreferredTimeOfDay().toLowerCase();
                
                for (TimeSlot slot : allAvailable) {
                    try {
                        int hour = Integer.parseInt(slot.getStartTime().split(":")[0]);
                        boolean match = false;
                        
                        if (preferred.contains("morning") && hour >= 6 && hour < 12) {
                            match = true;
                        } else if (preferred.contains("afternoon") && hour >= 12 && hour < 17) {
                            match = true;
                        } else if (preferred.contains("evening") && hour >= 17 && hour < 22) {
                            match = true;
                        }

                        if (match) {
                            recommendations.add(slot);
                        }
                    } catch (Exception ex) {
                        // ignore parsing error
                    }
                }
                
                if (recommendations.isEmpty()) {
                    recommendations = allAvailable; // fallback if no exact matches
                }
            }

            List<Map<String, Object>> enrichedSlots = new ArrayList<>();
            for (TimeSlot slot : recommendations) {
                Map<String, Object> slotMap = new HashMap<>();
                slotMap.put("id", slot.getId());
                slotMap.put("providerId", slot.getProviderId());
                slotMap.put("slotDate", slot.getSlotDate());
                slotMap.put("startTime", slot.getStartTime());
                slotMap.put("endTime", slot.getEndTime());
                
                Users provider = userRepo.findById(slot.getProviderId()).orElse(null);
                if (provider != null) {
                    slotMap.put("providerName", provider.getFullname());
                    slotMap.put("providerEmail", provider.getEmail());
                } else {
                    slotMap.put("providerName", "Unknown Provider");
                }
                enrichedSlots.add(slotMap);
            }

            response.put("code", 200);
            response.put("suggestions", enrichedSlots);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }
}
