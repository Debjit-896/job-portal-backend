package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.UserSkill;
import com.debjitpal.jobportal.user_service.entity.UserSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {
    List<UserSkill> findByIdUserId(UUID userId);
    void deleteByIdUserId(UUID userId);
}
