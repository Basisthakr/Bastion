package com.Basisttha.Bastion.DTO;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotNull
    private UUID recipientId;
    @NotBlank(message = "The message must not be blank")
    private String cipherText;
    @NotBlank(message = "The nonce must not be blank")
    private String nonce;
}
