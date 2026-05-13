package com.Basisttha.Bastion.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder//?
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(nullable=false, unique = true)
    private String username;
    @Column(nullable = false, unique = true, columnDefinition="TEXT")//?
    private String publicKey;
    @Enumerated(EnumType.STRING)//?
    private Status status;
    private LocalDateTime lastSeen;//Changed from Date to LocalDateTime
    private LocalDateTime createdAt;
    private LocalDateTime keyRotatedAt;

    @PrePersist
    void setCreation(){
        this.createdAt = LocalDateTime.now();
        this.status = Status.OFFLINE;
    }
}
