package com.Basisttha.Bastion.DTO;

import java.util.UUID;

import lombok.Data;

@Data
public class SendMessageRequest {
    private UUID recipientId;
    private String cipherText;
    private String nonce;
}
