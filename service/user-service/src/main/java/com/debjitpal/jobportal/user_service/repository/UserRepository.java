package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
