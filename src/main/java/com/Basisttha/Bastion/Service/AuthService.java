package com.Basisttha.Bastion.Service;

import com.Basisttha.Bastion.DTO.*;
import com.Basisttha.Bastion.Model.*;
import com.Basisttha.Bastion.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final AuthChallengeRepository authRepo;
    private final RevokedTokenRepository revokedTokenRepository;
    private final RecoveryKeyRepository recoveryKeyRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    //REGISTER 
    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User user = User.builder()
                .username(req.getUsername())
                .publicKey(req.getPublicKey())
                .build();

        User saved = userRepo.save(user);

        List<String> plainKeys = generateAndSaveRecoveryKeys(saved);

        return new RegisterResponse(saved.getId(), saved.getUsername(), plainKeys);
    }

    //CHALLENGE
    public ChallengeResponse createChallenge(ChallengeRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        authRepo.findByUserIdAndUsedFalse(user.getId())
                .ifPresent(existing -> {
                    existing.setUsed(true);
                    authRepo.save(existing);
                });

        String nonce = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(120);

        AuthChallenges challenge = AuthChallenges.builder()
                .user(user)
                .nonce(nonce)
                .expiresAt(expiresAt)
                .build();

        authRepo.save(challenge);
        return new ChallengeResponse(nonce, expiresAt.toString());
    }

    //VERIFY
    public AuthResponse verify(VerifyRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthChallenges challenge = authRepo.findByUserIdAndUsedFalse(user.getId())
                .orElseThrow(() -> new RuntimeException("No active challenge found"));

        if (LocalDateTime.now().isAfter(challenge.getExpiresAt())) {
            throw new RuntimeException("Challenge expired");
        }

        boolean valid = verifySignature(
                challenge.getNonce(),
                req.getSignature(),
                user.getPublicKey()
        );

        if (!valid) {
            throw new RuntimeException("Invalid signature");
        }

        challenge.setUsed(true);
        authRepo.save(challenge);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    //LOGOUT
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        if (revokedTokenRepository.existsByToken(token)) {
            return;
        }

        LocalDateTime expiresAt = jwtService.extractExpiry(token);

        RevokedToken revokedToken = RevokedToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .revokedAt(LocalDateTime.now())
                .build();

        revokedTokenRepository.save(revokedToken);
    }

    //RECOVER ACCOUNT
    @Transactional
    public AuthResponse recoverAccount(RecoverAccountRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RecoveryKey> activeKeys = recoveryKeyRepository
                .findByUserIdAndInvalidatedFalseAndUsedFalse(user.getId());

        if (activeKeys.isEmpty()) {
            throw new RuntimeException("No active recovery keys found");
        }

        RecoveryKey matchedKey = activeKeys.stream()
                .filter(k -> passwordEncoder.matches(req.getRecoveryKey(), k.getKeyHash()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid recovery key"));

        matchedKey.setUsed(true);
        matchedKey.setUsedAt(LocalDateTime.now());
        recoveryKeyRepository.save(matchedKey);

        user.setPublicKey(req.getNewPublicKey());
        user.setKeyRotatedAt(LocalDateTime.now());
        userRepo.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    //ROTATE KEY
    @Transactional
    public void rotateKey(User currentUser, RotateKeyRequest req) {
        currentUser.setPublicKey(req.getNewPublicKey());
        currentUser.setKeyRotatedAt(LocalDateTime.now());
        userRepo.save(currentUser);
    }

    //REFRESH RECOVERY KEYS
    @Transactional
    public RecoveryKeyResponse refreshRecoveryKeys(User currentUser) {
        recoveryKeyRepository.invalidateAllByUserId(currentUser.getId());
        List<String> newKeys = generateAndSaveRecoveryKeys(currentUser);
        return new RecoveryKeyResponse(newKeys, "Recovery keys refreshed. Store these safely — they will not be shown again.");
    }

    //PRIVATE HELPERS
    private List<String> generateAndSaveRecoveryKeys(User user) {
        List<String> plainKeys = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            String plainKey = UUID.randomUUID().toString().replace("-", "");
            String hashed = passwordEncoder.encode(plainKey);

            RecoveryKey recoveryKey = RecoveryKey.builder()
                    .user(user)
                    .keyHash(hashed)
                    .build();

            recoveryKeyRepository.save(recoveryKey);
            plainKeys.add(plainKey);
        }

        return plainKeys;
    }

    private boolean verifySignature(String nonce, String signatureB64, String publicKeyB64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyB64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature sig = Signature.getInstance("Ed25519");
            sig.initVerify(publicKey);
            sig.update(nonce.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signatureB64);
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }
}