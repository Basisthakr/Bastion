package com.Basisttha.Bastion.DTO;

import lombok.Data;

@Data
public class RecoverAccountRequest {
    private String username;
    private String recoveryKey;
    private String newPublicKey;
}
