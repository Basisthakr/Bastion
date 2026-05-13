package com.Basisttha.Bastion.DTO;

import lombok.Data;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class VerifyRequest {
    @NotNull(message = "User Id must not be null")
    private UUID userId;
    @NotBlank(message = "Signature must not be blank")
    private String signature;
}
