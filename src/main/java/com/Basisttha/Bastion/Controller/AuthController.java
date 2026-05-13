package com.Basisttha.Bastion.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Basisttha.Bastion.DTO.AuthResponse;
import com.Basisttha.Bastion.DTO.ChallengeRequest;
import com.Basisttha.Bastion.DTO.ChallengeResponse;
import com.Basisttha.Bastion.DTO.RegisterRequest;
import com.Basisttha.Bastion.DTO.RegisterResponse;
import com.Basisttha.Bastion.DTO.VerifyRequest;
import com.Basisttha.Bastion.Service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> Register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<ChallengeResponse> createChallenge(@Valid @RequestBody ChallengeRequest req) {
        return ResponseEntity.ok(authService.createChallenge(req));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody VerifyRequest req) {
        return ResponseEntity.ok(authService.verify(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.ok("Logged out successfully");
    }
}
