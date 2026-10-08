package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.UserLanguage;
import com.debjitpal.jobportal.user_service.entity.UserLanguageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, UserLanguageId> {
    List<UserLanguage> findByIdUserId(UUID userId);
    void deleteByIdUserId(UUID userId);
}
