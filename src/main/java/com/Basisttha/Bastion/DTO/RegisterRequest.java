package com.Basisttha.Bastion.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min=2, max = 50, message="The username must between 2 and 50 letters, both inclusive")
    private String username;

    @NotBlank(message="Public key is required")
    private String publicKey;
}
