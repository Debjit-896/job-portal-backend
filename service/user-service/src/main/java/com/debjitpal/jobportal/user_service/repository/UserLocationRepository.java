package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, UUID> {
    List<UserLocation> findByUserId(UUID userId);
}
