package com.example.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.model.TimeSlot;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
	List<TimeSlot> findByProviderId(String providerId);
	List<TimeSlot> findBySlotDateAndIsAvailableTrue(String slotDate);
	List<TimeSlot> findByIsAvailableTrue();

	@Query("SELECT t FROM TimeSlot t WHERE t.providerId = ?1 AND t.slotDate = ?2 AND " +
	       "((t.startTime <= ?3 AND t.endTime > ?3) OR " +
	       "(t.startTime < ?4 AND t.endTime >= ?4) OR " +
	       "(t.startTime >= ?3 AND t.endTime <= ?4))")
	List<TimeSlot> findOverlappingSlots(String providerId, String slotDate, String startTime, String endTime);
}
