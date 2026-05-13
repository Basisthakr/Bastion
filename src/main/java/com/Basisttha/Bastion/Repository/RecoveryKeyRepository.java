package com.Basisttha.Bastion.Repository;

import com.Basisttha.Bastion.Model.RecoveryKey;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecoveryKeyRepository extends JpaRepository<RecoveryKey, UUID>{
    List<RecoveryKey> findByUserIdandInvalidatedFalseAndUsedFalse(UUID userId);
    boolean ExistsByUserIdAndInvalidatedFalse(UUID userId);
}
