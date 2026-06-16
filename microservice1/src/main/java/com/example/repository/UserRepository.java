package com.example.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
	
	public Users findFirstByEmailAndPassword(String email, String password);
	public Users findFirstByEmail(String email);
	public Users findFirstByFullname(String fullname);
	
	@Query("SELECT u FROM Users u WHERE LOWER(u.fullname) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', ?1, '%'))")
	public List<Users> searchUsers(String val);
}
