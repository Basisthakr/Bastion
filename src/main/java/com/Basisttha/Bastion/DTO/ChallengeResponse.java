package com.Basisttha.Bastion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChallengeResponse {
    private String nonce;
    private String expiresAt;
}
