package com.Basisttha.Bastion.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Basisttha.Bastion.DTO.AuthResponse;
import com.Basisttha.Bastion.DTO.ChallengeRequest;
import com.Basisttha.Bastion.DTO.ChallengeResponse;
import com.Basisttha.Bastion.DTO.RecoverAccountRequest;
import com.Basisttha.Bastion.DTO.RecoveryKeyResponse;
import com.Basisttha.Bastion.DTO.RegisterRequest;
import com.Basisttha.Bastion.DTO.RegisterResponse;
import com.Basisttha.Bastion.DTO.RotateKeyRequest;
import com.Basisttha.Bastion.DTO.VerifyRequest;
import com.Basisttha.Bastion.Model.User;
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

    @PostMapping("/challenge")
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

    @PutMapping("/rotate-key")
    public ResponseEntity<String> rotateKey(
            @RequestBody RotateKeyRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        authService.rotateKey(currentUser, request);
        return ResponseEntity.ok("Key rotated successfully. All previous sessions are now invalid.");
    }

    @PostMapping("/refresh-recovery-keys")
    public ResponseEntity<RecoveryKeyResponse> refreshRecoveryKeys() {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(authService.refreshRecoveryKeys(currentUser));
    }

    @PostMapping("/recover")
    public ResponseEntity<AuthResponse> recover(@RequestBody RecoverAccountRequest request) {
        return ResponseEntity.ok(authService.recoverAccount(request));
    }
}
