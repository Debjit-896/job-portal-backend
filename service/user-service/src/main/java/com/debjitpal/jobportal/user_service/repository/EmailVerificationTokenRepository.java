package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.EmailVerificationToken;
import com.debjitpal.jobportal.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    @Query("SELECT t FROM EmailVerificationToken t JOIN FETCH t.user WHERE t.tokenHash = :tokenHash")
    Optional<EmailVerificationToken> findByTokenHashWithUser(@Param("tokenHash") String tokenHash);

    java.util.List<EmailVerificationToken> findByUser(User user);

    /** Delete all tokens (used or expired) for a given user — called before issuing a fresh OTP */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.user = :user")
    void deleteAllByUser(@Param("user") User user);

    /** Cleanup job: remove every token that has already expired across all users */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteAllExpiredBefore(@Param("now") LocalDateTime now);
}
