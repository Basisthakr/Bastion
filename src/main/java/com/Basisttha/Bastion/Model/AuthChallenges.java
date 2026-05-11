package com.Basisttha.Bastion.Model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "auth_challenges")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthChallenges {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable= false, columnDefinition = "TEXT")//String in sql means varchar(255), but nonce/actual texts can be larger, so TEXT means unlimited space for string
    private String nonce;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used;

    @PrePersist
    void setUsed(){
        this.used = false;
    }
}
