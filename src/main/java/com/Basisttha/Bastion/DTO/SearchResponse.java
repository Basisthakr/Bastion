package com.Basisttha.Bastion.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResponse {
    private UUID userId;
    private String username;
}
