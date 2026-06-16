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

import com.example.model.Appointment;
import com.example.service.AppointmentService;

@RestController
@RequestMapping("/bookingservice")
@CrossOrigin
public class AppointmentController {

    @Autowired
    AppointmentService appService;

    @Autowired
    com.example.service.SuggestionService suggestionService;

    @PostMapping("/createappointment")
    public Object createAppointment(@RequestBody Appointment app, @RequestHeader("Token") String token) {
        return appService.createAppointment(app, token);
    }

    @GetMapping("/getclienthistory")
    public Object getClientHistory(@RequestHeader("Token") String token) {
        return appService.getClientHistory(token);
    }

    @GetMapping("/getproviderschedule")
    public Object getProviderSchedule(@RequestHeader("Token") String token) {
        return appService.getProviderSchedule(token);
    }

    @DeleteMapping("/cancelappointment/{id}")
    public Object cancelAppointment(@PathVariable("id") String id, @RequestHeader("Token") String token) {
        return appService.cancelAppointment(id, token);
    }

    @GetMapping("/suggestions")
    public Object getSuggestions(@RequestHeader("Token") String token) {
        return suggestionService.getSuggestions(token);
    }
}
