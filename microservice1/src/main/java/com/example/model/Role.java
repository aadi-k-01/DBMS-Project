package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
	@Id
	private String id;
	private int role;
	private String rolename;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public int getRole() { return role; }
	public void setRole(int role) { this.role = role; }
	public String getRolename() { return rolename; }
	public void setRolename(String rolename) { this.rolename = rolename; }
}
