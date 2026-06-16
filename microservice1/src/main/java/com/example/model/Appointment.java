package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String clientId;
	private String timeSlotId;
	private String status; // BOOKED, CANCELLED, COMPLETED
	private String bookingTime; // YYYY-MM-DD HH:MM:SS
	private String notes;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getClientId() { return clientId; }
	public void setClientId(String clientId) { this.clientId = clientId; }
	public String getTimeSlotId() { return timeSlotId; }
	public void setTimeSlotId(String timeSlotId) { this.timeSlotId = timeSlotId; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public String getBookingTime() { return bookingTime; }
	public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
}
