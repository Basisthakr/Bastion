package com.Basisttha.Bastion.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "Search bar must not be blank")
    private String username;

    public SearchRequest(String username){
        this.username = username;
    }
}
