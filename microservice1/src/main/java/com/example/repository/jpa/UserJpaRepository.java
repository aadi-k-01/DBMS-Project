package com.example.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.jpa.UsersJpa;

@Repository
public interface UserJpaRepository extends JpaRepository<UsersJpa, String> {
}
