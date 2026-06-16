package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Users;
import com.example.service.UsersService;

@RestController
@RequestMapping("/authservice")
@CrossOrigin
public class UsersController {
	
	@Autowired
	UsersService service;
	
	@Autowired
	com.example.service.UserPreferenceService prefService;
	
	@GetMapping("/")
	public String test() {
		return "Spring Boot - Backend Running!";
	}
	
	@PostMapping("/signin")
	public Object signin(@RequestBody Map<String, String> u1) {
		return service.signinService(u1);
	}
	
	@PostMapping("/signup")
	public Object signup(@RequestBody Users u1) {
		return service.signupService(u1);
	}
	
	@GetMapping("/uinfo")
	public Object uinfo(@RequestHeader("Token") String token) {
		return service.uinfo(token);
	}
	
	@GetMapping("/profile")
	public Object getProfile(@RequestHeader("Token") String token) {
		return service.getProfile(token);
	}
	
	@GetMapping("/getallusers/{page}/{limit}")
	public Object uinfo(@PathVariable("page") int page, @PathVariable("limit") int limit, @RequestHeader("Token") String token) {
		return service.getAllUsers(page, limit, token);
	}
	
	@GetMapping("/getuser/{id}")
	public Object getUser(@PathVariable("id") String id, @RequestHeader("Token") String token) {
		return service.getUserById(id, token);
	}
	
	@PostMapping("/saveuser")
	public Object saveUser(@RequestBody Users u1, @RequestHeader("Token") String token) {
		return service.saveUser(u1, token);
	}
	
	@PutMapping("/updateuser/{id}")
	public Object updateUser(@PathVariable("id") String id, @RequestBody Users u1, @RequestHeader("Token") String token) {
		return service.updateUser(id, u1, token);
	}
	
	@DeleteMapping("/deleteuser/{id}")
	public Object deleteUser(@PathVariable("id") String id, @RequestHeader("Token") String Token) {
		return service.deleteUser(id, Token);
	}
	
	@GetMapping("/searchuser/{val}")
	public Object searchUser(@PathVariable("val") String val, @RequestHeader("Token") String token) {
		return service.searchUsers(val, token);
	}

	@PostMapping("/preferences")
	public Object savePreference(@RequestBody com.example.model.mongo.UserPreference pref, @RequestHeader("Token") String token) {
		return prefService.savePreference(pref, token);
	}
	
	@GetMapping("/preferences")
	public Object getPreference(@RequestHeader("Token") String token) {
		return prefService.getPreference(token);
	}
}
