package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.TimeSlot;
import com.example.model.Users;
import com.example.repository.AppointmentRepository;
import com.example.repository.TimeSlotRepository;
import com.example.repository.UserRepository;

@Service
public class TimeSlotService {

    @Autowired
    TimeSlotRepository slotRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    AppointmentRepository appointmentRepo;

    @Autowired
    JWTService jwtService;

    public Object createSlot(TimeSlot slot, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            int role = Integer.parseInt(parsedJWT.get("role"));
            
            if (role != 2 && role != 3) {
                response.put("code", 403);
                response.put("message", "Access Denied: Only service providers can manage availability slots.");
                return response;
            }

            Users provider = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (provider == null) {
                response.put("code", 404);
                response.put("message", "Provider not found.");
                return response;
            }

            slot.setProviderId(provider.getId());
            slot.setAvailable(true);

            if (slot.getStartTime().compareTo(slot.getEndTime()) >= 0) {
                response.put("code", 400);
                response.put("message", "Start time must be before end time.");
                return response;
            }

            List<TimeSlot> overlapping = slotRepo.findOverlappingSlots(
                slot.getProviderId(), 
                slot.getSlotDate(), 
                slot.getStartTime(), 
                slot.getEndTime()
            );

            if (!overlapping.isEmpty()) {
                response.put("code", 409);
                response.put("message", "Scheduling Conflict: You already have an overlapping slot on this date.");
                return response;
            }

            slotRepo.save(slot);
            response.put("code", 200);
            response.put("message", "Time slot created successfully.");
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object getProviderSlots(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users provider = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (provider == null) {
                response.put("code", 404);
                response.put("message", "Provider not found.");
                return response;
            }

            List<TimeSlot> slots = slotRepo.findByProviderId(provider.getId());
            response.put("code", 200);
            response.put("slots", slots);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object getAllAvailableSlots(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            jwtService.validateJWT(token);
            List<TimeSlot> slots = slotRepo.findByIsAvailableTrue();
            
            List<Map<String, Object>> enrichedSlots = new ArrayList<>();
            for (TimeSlot slot : slots) {
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
                    slotMap.put("providerPhone", provider.getPhone());
                } else {
                    slotMap.put("providerName", "Unknown Provider");
                }
                enrichedSlots.add(slotMap);
            }

            response.put("code", 200);
            response.put("slots", enrichedSlots);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object deleteSlot(String slotId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            jwtService.validateJWT(token);
            TimeSlot slot = slotRepo.findById(slotId).orElse(null);
            if (slot == null) {
                response.put("code", 404);
                response.put("message", "Time slot not found.");
                return response;
            }

            if (appointmentRepo.findByTimeSlotId(slotId) != null) {
                response.put("code", 400);
                response.put("message", "Cannot delete a slot that has a booked appointment. Please cancel the appointment first.");
                return response;
            }

            slotRepo.delete(slot);
            response.put("code", 200);
            response.put("message", "Time slot deleted successfully.");
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }
}
