package com.Basisttha.Bastion.Repository;

import com.Basisttha.Bastion.Model.RecoveryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecoveryKeyRepository extends JpaRepository<RecoveryKey, UUID> {
    List<RecoveryKey> findByUserIdAndInvalidatedFalseAndUsedFalse(UUID userId);
    boolean existsByUserIdAndInvalidatedFalse(UUID userId);

    @Modifying
    @Query("UPDATE RecoveryKey r SET r.invalidated = true WHERE r.user.id = :userId")
    void invalidateAllByUserId(@Param("userId") UUID userId);
}