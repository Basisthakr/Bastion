package com.Basisttha.Bastion.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Basisttha.Bastion.DTO.AuthResponse;
import com.Basisttha.Bastion.DTO.ChallengeRequest;
import com.Basisttha.Bastion.DTO.ChallengeResponse;
import com.Basisttha.Bastion.DTO.RegisterRequest;
import com.Basisttha.Bastion.DTO.RegisterResponse;
import com.Basisttha.Bastion.DTO.VerifyRequest;
import com.Basisttha.Bastion.Service.AuthService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> Register(@RequestBody RegisterRequest req){
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<ChallengeResponse> createChallenge(@RequestBody ChallengeRequest req){
        return ResponseEntity.ok(authService.createChallenge(req));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@RequestBody VerifyRequest req){
        return ResponseEntity.ok(authService.verify(req));
    }
}
