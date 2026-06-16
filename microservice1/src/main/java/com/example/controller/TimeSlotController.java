package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.TimeSlot;
import com.example.service.TimeSlotService;

@RestController
@RequestMapping("/slotsservice")
@CrossOrigin
public class TimeSlotController {

    @Autowired
    TimeSlotService slotService;

    @PostMapping("/createslot")
    public Object createSlot(@RequestBody TimeSlot slot, @RequestHeader("Token") String token) {
        return slotService.createSlot(slot, token);
    }

    @GetMapping("/getproviderslots")
    public Object getProviderSlots(@RequestHeader("Token") String token) {
        return slotService.getProviderSlots(token);
    }

    @GetMapping("/getavailableslots")
    public Object getAllAvailableSlots(@RequestHeader("Token") String token) {
        return slotService.getAllAvailableSlots(token);
    }

    @DeleteMapping("/deleteslot/{id}")
    public Object deleteSlot(@PathVariable("id") String id, @RequestHeader("Token") String token) {
        return slotService.deleteSlot(id, token);
    }
}
