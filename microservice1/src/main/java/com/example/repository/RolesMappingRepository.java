package com.example.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.RolesMapping;

@Repository
public interface RolesMappingRepository extends MongoRepository<RolesMapping, String> {
	List<RolesMapping> findByRole(int role);
}
