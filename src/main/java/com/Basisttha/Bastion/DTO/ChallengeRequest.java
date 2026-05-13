package com.Basisttha.Bastion.DTO;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChallengeRequest {
    @NotNull
    private UUID userId;
}
