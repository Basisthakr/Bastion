package com.Basisttha.Bastion.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Basisttha.Bastion.Model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByUsernameIgnoreCase(String username);
}
