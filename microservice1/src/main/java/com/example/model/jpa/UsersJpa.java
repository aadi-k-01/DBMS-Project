package com.example.model.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_tab")
public class UsersJpa {
	@Id
	private String id;
	private String email;
	private String fullname;
	private String phone;
	private String password;
	private int role;
	private int status;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getFullname() { return fullname; }
	public void setFullname(String fullname) { this.fullname = fullname; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public int getRole() { return role; }
	public void setRole(int role) { this.role = role; }
	public int getStatus() { return status; }
	public void setStatus(int status) { this.status = status; }
}
