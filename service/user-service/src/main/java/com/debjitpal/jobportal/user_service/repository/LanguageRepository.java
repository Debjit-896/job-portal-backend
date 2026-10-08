package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    Optional<Language> findByNameIgnoreCase(String name);
}
