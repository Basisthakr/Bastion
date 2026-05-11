package com.Basisttha.Bastion.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class VerifyRequest {
    private UUID userId;
    private String signature;
}
