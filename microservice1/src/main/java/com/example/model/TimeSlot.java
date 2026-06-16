package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_slots")
public class TimeSlot {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String providerId;
	private String slotDate; // YYYY-MM-DD
	private String startTime; // HH:MM
	private String endTime; // HH:MM
	private boolean isAvailable = true;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getProviderId() { return providerId; }
	public void setProviderId(String providerId) { this.providerId = providerId; }
	public String getSlotDate() { return slotDate; }
	public void setSlotDate(String slotDate) { this.slotDate = slotDate; }
	public String getStartTime() { return startTime; }
	public void setStartTime(String startTime) { this.startTime = startTime; }
	public String getEndTime() { return endTime; }
	public void setEndTime(String endTime) { this.endTime = endTime; }
	public boolean isAvailable() { return isAvailable; }
	public void setAvailable(boolean available) { this.isAvailable = available; }
}
