package com.example.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Menu;
import java.util.List;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
	Menu findByMid(int mid);
}
