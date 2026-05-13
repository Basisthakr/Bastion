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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder//?
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;//what does default mean and how to get it here

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false, columnDefinition="TEXT")
    private String cipherText;
    
    @Column(nullable = false, columnDefinition="TEXT")
    private String nonce;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    @PrePersist
    void setStuff(){
        this.deliveryStatus = DeliveryStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
