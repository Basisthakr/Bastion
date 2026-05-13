package com.Basisttha.Bastion.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecoveryKeyResponse {
    private List<String> recoveryKeys;
    private String message;
}
