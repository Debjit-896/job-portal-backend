package com.debjitpal.jobportal.user_service.repository;

import com.debjitpal.jobportal.user_service.entity.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, UUID> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);
    List<OAuthAccount> findByUserId(UUID userId);
    Optional<OAuthAccount> findByUserIdAndProvider(UUID userId, String provider);
}
