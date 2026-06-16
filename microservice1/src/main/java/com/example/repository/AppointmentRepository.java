package com.example.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
	List<Appointment> findByClientId(String clientId);
	Appointment findByTimeSlotId(String timeSlotId);
}
