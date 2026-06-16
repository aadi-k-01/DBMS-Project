package com.example.config;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.model.Menu;
import com.example.model.Role;
import com.example.model.RolesMapping;
import com.example.model.TimeSlot;
import com.example.model.Users;
import com.example.repository.TimeSlotRepository;
import com.example.repository.UserRepository;
import com.example.repository.RoleRepository;
import com.example.repository.MenuRepository;
import com.example.repository.RolesMappingRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TimeSlotRepository slotRepo;
    
    @Autowired
    private RoleRepository roleRepo;
    
    @Autowired
    private MenuRepository menuRepo;
    
    @Autowired
    private RolesMappingRepository rolesMappingRepo;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        if (roleRepo.findByRole(1) == null) {
            saveRole(1, "Client");
        }
        if (roleRepo.findByRole(2) == null) {
            saveRole(2, "Provider");
        }
        if (roleRepo.findByRole(3) == null) {
            saveRole(3, "Admin");
        }

        // 2. Seed Menus
        if (menuRepo.findByMid(3) == null) {
            saveMenu(3, "Appointments", "task.png");
        }
        if (menuRepo.findByMid(4) == null) {
            saveMenu(4, "User Manager", "users.png");
        }
        if (menuRepo.findByMid(5) == null) {
            saveMenu(5, "My Profile", "user.png");
        }

        // 3. Seed Roles Mapping
        rolesMappingRepo.deleteAll();
        
        saveRoleMapping(3, 1);
        saveRoleMapping(5, 1);
        saveRoleMapping(3, 2);
        saveRoleMapping(5, 2);
        saveRoleMapping(3, 3);
        saveRoleMapping(4, 3);
        saveRoleMapping(5, 3);

        // 4. Seed Default Users
        if (userRepo.findFirstByEmail("admin@example.com") == null) {
            saveUser("Admin", "admin@example.com", "1234567890", "admin", 3);
        }
        
        Users john = userRepo.findFirstByFullname("Dr. John Smith");
        if (john == null) {
            john = saveUser("Dr. John Smith", "john@example.com", "9988776655", "john", 2);
        }
        syncProviderToMongo(john, "Cardiologist", "Experienced heart doctor offering general consultation and cardiovascular checkups in the morning.");

        Users sarah = userRepo.findFirstByFullname("Dr. Sarah Lee");
        if (sarah == null) {
            sarah = saveUser("Dr. Sarah Lee", "sarah@example.com", "8877665544", "sarah", 2);
        }
        syncProviderToMongo(sarah, "Dentist", "Specialist in dental checkup, teeth cleaning, and orthodontist care. Available for afternoon consultations.");

        Users alice = userRepo.findFirstByFullname("Dr. Alice Brown");
        if (alice == null) {
            alice = saveUser("Dr. Alice Brown", "alice@example.com", "7766554433", "alice", 2);
        }
        syncProviderToMongo(alice, "Therapist", "Professional counselor offering psychologist sessions and mental therapy in the evening.");

        if (userRepo.findFirstByEmail("jane@example.com") == null) {
            saveUser("Jane Doe", "jane@example.com", "5566778899", "jane", 1);
        }
        if (userRepo.findFirstByEmail("bob@example.com") == null) {
            saveUser("Bob Johnson", "bob@example.com", "6677889900", "bob", 1);
        }

        // 5. Seed default time slots if none exist
        if (slotRepo.count() == 0) {
            if (john != null) {
                saveSlot(john.getId(), "2026-06-13", "09:00", "10:00");
                saveSlot(john.getId(), "2026-06-13", "14:00", "15:00");
            }
            if (sarah != null) {
                saveSlot(sarah.getId(), "2026-06-14", "10:00", "11:00");
                saveSlot(sarah.getId(), "2026-06-14", "11:30", "12:30");
            }
            if (alice != null) {
                saveSlot(alice.getId(), "2026-06-14", "17:00", "18:00");
            }
        }
        
        System.out.println(">>> Database Seeding Completed Successfully! <<<");
    }

    private void saveRole(int roleId, String roleName) {
        Role role = new Role();
        role.setRole(roleId);
        role.setRolename(roleName);
        roleRepo.save(role);
    }

    private void saveMenu(int mid, String menuName, String icon) {
        Menu menu = new Menu();
        menu.setMid(mid);
        menu.setMenu(menuName);
        menu.setMicon(icon);
        menuRepo.save(menu);
    }

    private void saveRoleMapping(int mid, int role) {
        RolesMapping mapping = new RolesMapping();
        mapping.setMid(mid);
        mapping.setRole(role);
        rolesMappingRepo.save(mapping);
    }

    private Users saveUser(String fullname, String email, String phone, String password, int role) {
        Users user = new Users();
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(1);
        return userRepo.save(user);
    }

    private void syncProviderToMongo(Users provider, String specialty, String bio) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            Map<String, Object> payload = new HashMap<>();
            payload.put("providerId", provider.getId());
            payload.put("providerName", provider.getFullname());
            payload.put("specialty", specialty);
            payload.put("bio", bio);

            String jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);

            String nodeUrl = System.getenv("NODE_SERVICE_URL");
            if (nodeUrl == null) {
                nodeUrl = "http://localhost:8002";
            }
            
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(nodeUrl + "/node/providers"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println("Syncing provider " + provider.getFullname() + " to MongoDB...");
        } catch (Exception e) {
            System.err.println("Failed to sync provider to MongoDB: " + e.getMessage());
        }
    }

    private void saveSlot(String providerId, String date, String start, String end) {
        TimeSlot slot = new TimeSlot();
        slot.setProviderId(providerId);
        slot.setSlotDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(true);
        slotRepo.save(slot);
    }
}
