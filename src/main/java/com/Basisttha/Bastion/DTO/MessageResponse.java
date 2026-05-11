package com.Basisttha.Bastion.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String cipherText;
    private String nonce;
    private String deliveryStatus;
    private String createdAt;
}
