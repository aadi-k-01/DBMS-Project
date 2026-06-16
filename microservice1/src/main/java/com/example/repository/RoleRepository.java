package com.example.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
	Role findByRole(int role);
}
