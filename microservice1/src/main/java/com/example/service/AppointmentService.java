package com.example.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Appointment;
import com.example.model.TimeSlot;
import com.example.model.Users;
import com.example.repository.AppointmentRepository;
import com.example.repository.TimeSlotRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepo;

    @Autowired
    TimeSlotRepository slotRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    JWTService jwtService;

    @Autowired
    com.example.repository.mongo.BookingLogRepository bookingLogRepo;

    public Object createAppointment(Appointment app, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users client = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (client == null) {
                response.put("code", 404);
                response.put("message", "Client profile not found.");
                return response;
            }

            TimeSlot slot = slotRepo.findById(app.getTimeSlotId()).orElse(null);
            if (slot == null) {
                response.put("code", 404);
                response.put("message", "Selected availability slot does not exist.");
                return response;
            }

            if (!slot.isAvailable()) {
                response.put("code", 400);
                response.put("message", "This slot has already been booked by another user.");
                return response;
            }

            slot.setAvailable(false);
            slotRepo.save(slot);

            app.setClientId(client.getId());
            app.setStatus("BOOKED");
            app.setBookingTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            Appointment savedApp = appointmentRepo.save(app);

            Users provider = userRepo.findById(slot.getProviderId()).orElse(null);
            String providerName = provider != null ? provider.getFullname() : "Unknown";

            String logMsg = String.format("User '%s' booked an appointment with '%s' on %s at %s.",
                client.getFullname(), providerName, slot.getSlotDate(), slot.getStartTime());
            logToMongo("CREATE", client.getId(), savedApp.getId(), logMsg);

            response.put("code", 200);
            response.put("message", "Appointment booked successfully!");
            response.put("appointment", savedApp);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object getClientHistory(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users client = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (client == null) {
                response.put("code", 404);
                response.put("message", "Client profile not found.");
                return response;
            }

            List<Appointment> list = appointmentRepo.findByClientId(client.getId());
            List<Map<String, Object>> enrichedList = new ArrayList<>();

            for (Appointment app : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", app.getId());
                item.put("clientId", app.getClientId());
                item.put("timeSlotId", app.getTimeSlotId());
                item.put("status", app.getStatus());
                item.put("bookingTime", app.getBookingTime());
                item.put("notes", app.getNotes());

                TimeSlot slot = slotRepo.findById(app.getTimeSlotId()).orElse(null);
                if (slot != null) {
                    item.put("slotDate", slot.getSlotDate());
                    item.put("startTime", slot.getStartTime());
                    item.put("endTime", slot.getEndTime());

                    Users provider = userRepo.findById(slot.getProviderId()).orElse(null);
                    if (provider != null) {
                        item.put("providerName", provider.getFullname());
                        item.put("providerEmail", provider.getEmail());
                        item.put("providerPhone", provider.getPhone());
                    }
                }
                enrichedList.add(item);
            }

            response.put("code", 200);
            response.put("appointments", enrichedList);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object getProviderSchedule(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users provider = userRepo.findFirstByFullname(parsedJWT.get("username"));
            if (provider == null) {
                response.put("code", 404);
                response.put("message", "Provider profile not found.");
                return response;
            }

            List<TimeSlot> slots = slotRepo.findByProviderId(provider.getId());
            List<Map<String, Object>> bookings = new ArrayList<>();

            for (TimeSlot slot : slots) {
                Appointment app = appointmentRepo.findByTimeSlotId(slot.getId());
                if (app != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", app.getId());
                    item.put("clientId", app.getClientId());
                    item.put("timeSlotId", app.getTimeSlotId());
                    item.put("status", app.getStatus());
                    item.put("bookingTime", app.getBookingTime());
                    item.put("notes", app.getNotes());
                    item.put("slotDate", slot.getSlotDate());
                    item.put("startTime", slot.getStartTime());
                    item.put("endTime", slot.getEndTime());

                    Users client = userRepo.findById(app.getClientId()).orElse(null);
                    if (client != null) {
                        item.put("clientName", client.getFullname());
                        item.put("clientEmail", client.getEmail());
                        item.put("clientPhone", client.getPhone());
                    }
                    bookings.add(item);
                }
            }

            response.put("code", 200);
            response.put("appointments", bookings);
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    public Object cancelAppointment(String appointmentId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> parsedJWT = jwtService.validateJWT(token);
            Users caller = userRepo.findFirstByFullname(parsedJWT.get("username"));

            Appointment app = appointmentRepo.findById(appointmentId).orElse(null);
            if (app == null) {
                response.put("code", 404);
                response.put("message", "Appointment not found.");
                return response;
            }

            if (!"BOOKED".equals(app.getStatus())) {
                response.put("code", 400);
                response.put("message", "Appointment is already cancelled or completed.");
                return response;
            }

            TimeSlot slot = slotRepo.findById(app.getTimeSlotId()).orElse(null);
            if (slot == null) {
                response.put("code", 404);
                response.put("message", "Associated slot not found.");
                return response;
            }

            if (!caller.getId().equals(app.getClientId()) && !caller.getId().equals(slot.getProviderId()) && caller.getRole() != 3) {
                response.put("code", 403);
                response.put("message", "Access Denied: You cannot cancel this appointment.");
                return response;
            }

            app.setStatus("CANCELLED");
            appointmentRepo.save(app);

            slot.setAvailable(true);
            slotRepo.save(slot);

            String logMsg = String.format("Appointment ID %s cancelled by user '%s'. Slot date: %s, Time: %s.",
                appointmentId, caller.getFullname(), slot.getSlotDate(), slot.getStartTime());
            logToMongo("CANCEL", caller.getId(), appointmentId, logMsg);

            response.put("code", 200);
            response.put("message", "Appointment cancelled successfully.");
            return response;
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", e.getMessage());
            return response;
        }
    }

    private void logToMongo(String action, String userId, String appointmentId, String details) {
        try {
            com.example.model.mongo.BookingLog log = new com.example.model.mongo.BookingLog();
            log.setAction(action);
            log.setUserId(userId);
            log.setAppointmentId(appointmentId);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            bookingLogRepo.save(log);
        } catch (Exception e) {
            System.err.println("Failed to write audit log to MongoDB locally: " + e.getMessage());
        }
    }
}
