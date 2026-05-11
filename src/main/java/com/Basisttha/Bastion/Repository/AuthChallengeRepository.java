package com.Basisttha.Bastion.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Basisttha.Bastion.Model.AuthChallenges;

@Repository
public interface AuthChallengeRepository extends JpaRepository<AuthChallenges, UUID>{
    Optional<AuthChallenges> findByUserIdAndUsedFalse(UUID id);
}
