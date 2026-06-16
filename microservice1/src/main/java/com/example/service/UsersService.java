package com.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.model.Users;
import com.example.model.Role;
import com.example.model.RolesMapping;
import com.example.model.Menu;
import com.example.repository.UserRepository;
import com.example.repository.MenuRepository;
import com.example.repository.RoleRepository;
import com.example.repository.RolesMappingRepository;

@Service
public class UsersService {
	
	@Autowired
	UserRepository repo;
	
	@Autowired
	MenuRepository menuRepo;
	@Autowired
	RoleRepository roleRepo;
	@Autowired
	RolesMappingRepository rolesMappingRepo;
	
	@Autowired
	JWTService jwtService;
			
	public Object signupService(Users u1) {
		Map<String, Object> response = new HashMap<>();
		try {
			Users existing = repo.findFirstByEmail(u1.getEmail());
			if(existing != null) {
				response.put("code", 501);
				response.put("message", "user already exist");
			}
			else {
				u1.setRole(1);
				u1.setStatus(1);
				repo.save(u1);
				response.put("code", 200);
				response.put("message", "User Registerd Successfully");
			}
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object signinService(Map<String, String> u1) {
		Map<String, Object> response = new HashMap<>();
		try {
			Users user = repo.findFirstByEmailAndPassword(u1.get("username"), u1.get("password"));
			if(user == null) {
				response.put("code", 501);
				response.put("message", "Authentication Failed");
			} else {
				Map<String, String> jwtPayload = new HashMap<>();
				jwtPayload.put("username", user.getFullname());
				jwtPayload.put("role", String.valueOf(user.getRole()));
				response.put("code", 200);
				response.put("jwt", jwtService.generateJWT(jwtPayload, String.valueOf(user.getRole())));
			}
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object uinfo(String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, String> parsedJWT = jwtService.validateJWT(token);
			Users u1 = repo.findFirstByFullname(parsedJWT.get("username"));
			
			List<RolesMapping> rms = rolesMappingRepo.findByRole(Integer.parseInt(parsedJWT.get("role")));
			List<Object> menulist = new ArrayList<>();
			for(RolesMapping rm : rms) {
				Menu m = menuRepo.findByMid(rm.getMid());
				if(m != null) menulist.add(m);
			}
			
			response.put("code", 200);
			response.put("fullname", u1.getFullname());
			response.put("menulist", menulist);
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object getProfile(String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, String> parsedJWT = jwtService.validateJWT(token);
			Users user = repo.findFirstByFullname(parsedJWT.get("username"));
			Role role = null;
			if (user != null) {
			    role = roleRepo.findByRole(user.getRole());
			}
			
			Map<String, Object> combined = new HashMap<>();
			combined.put("user", user);
			combined.put("role", role);
			
			response.put("code", 200);
			response.put("user", combined);
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object getAllUsers(int page, int limit, String token){
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			Pageable pageable = PageRequest.of(page-1, limit);
			Page<Users> users = repo.findAll(pageable);
			response.put("code", 200);
			response.put("page", page);
			response.put("size", limit);
			response.put("totalpages", users.getTotalPages());
			response.put("users", users.getContent());
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object getUserById(String id, String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			Users user = repo.findById(id).orElse(null);
			if(user == null) {
				throw new Exception("User Not Found");
			}
			response.put("code", 200);
			response.put("user", user);
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object saveUser(Users u1, String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			repo.save(u1);
			response.put("code", 200);
			response.put("message", "saved successfully");
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object updateUser(String id, Users u1, String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			Users user = repo.findById(id).orElse(null);
			if(user == null) {
				throw new Exception("user not exist");
			}
			user.setFullname(u1.getFullname());
			user.setPassword(u1.getPassword());
			user.setPhone(u1.getPhone());
			user.setEmail(u1.getEmail());
			repo.save(user);
			response.put("code", 200);
			response.put("message", "updated successfully");
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object deleteUser(String id, String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			Users user = repo.findById(id).orElse(null);
			if(user == null) {
				throw new Exception("user not exist");
			}
			repo.deleteById(id);
			response.put("code", 200);
			response.put("message", "deleted successfully");
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
	
	public Object searchUsers(String val, String token) {
		Map<String, Object> response = new HashMap<>();
		try {
			jwtService.validateJWT(token);
			List<Users> list = repo.searchUsers(val);
			response.put("code", 200);
			response.put("users", list);
			return response;
		} catch (Exception e) {
			response.put("code", 500);
			response.put("message", e.getMessage());
			return response;
		}
	}
}
