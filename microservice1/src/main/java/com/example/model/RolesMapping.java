package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles_mapping")
public class RolesMapping {
	@Id
	private String id;
	private int mid;
	private int role;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public int getMid() { return mid; }
	public void setMid(int mid) { this.mid = mid; }
	public int getRole() { return role; }
	public void setRole(int role) { this.role = role; }
}
