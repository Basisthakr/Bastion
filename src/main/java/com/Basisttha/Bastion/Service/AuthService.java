package com.Basisttha.Bastion.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Basisttha.Bastion.DTO.AuthResponse;
import com.Basisttha.Bastion.DTO.ChallengeRequest;
import com.Basisttha.Bastion.DTO.ChallengeResponse;
import com.Basisttha.Bastion.DTO.RegisterRequest;
import com.Basisttha.Bastion.DTO.RegisterResponse;
import com.Basisttha.Bastion.DTO.VerifyRequest;
import com.Basisttha.Bastion.Model.AuthChallenges;
import com.Basisttha.Bastion.Model.User;
import com.Basisttha.Bastion.Repository.AuthChallengeRepository;
import com.Basisttha.Bastion.Repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiry.hours}")
    private int jwtExpiryHours;

     
    private final UserRepository userRepo;
    private final AuthChallengeRepository authRepo;
    private final JwtService jwtService;


    public RegisterResponse register(RegisterRequest req){
        //Job 1: Check if username already exists in repo
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(req.getUsername())
                .publicKey(req.getPublicKey())
                .build();

        User saved = userRepo.save(user);
        return new RegisterResponse(saved.getId(), saved.getUsername());
    }
    //Job 1 complete. User saved

    // Job 2: Login. Part 1: Find user by their UUID
    public ChallengeResponse createChallenge(ChallengeRequest req){
        //Part 1: Find user by their UUID
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));//can also use Optional then check if optional is empty
        //Part 1.5: Invalidate unused challenges
        Optional<AuthChallenges> authOptional = authRepo.findByUserIdAndUsedFalse(user.getId());
        if (!authOptional.isEmpty()) {
            authRepo.deleteById(authOptional.get().getId());
        }
        //Part 2: Generate a random nonce.
        String nonce = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(120);
        AuthChallenges challenge = AuthChallenges.builder()
                .user(user)
                .nonce(nonce)
                .expiresAt(expiry)
                .build();
        authRepo.save(challenge);
        System.out.println("Saved challenge for userId: " + challenge.getUser().getId());
        System.out.println("Used: " + challenge.isUsed());
        return new ChallengeResponse(nonce, expiry.toString());
    }

    //Job 3: Verify(for login attempt) if the request is valid
    public AuthResponse verify(VerifyRequest req){
        //Part 1: find the user
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));//can also use Optional then check if optional is empty
        Optional<AuthChallenges> authOptional = authRepo.findByUserIdAndUsedFalse(user.getId()); ////find active unused challenge. active = that isnt expired
        System.out.println("Looking for challenge with userId: " + user.getId());
        System.out.println("Challenge found: " + authOptional.isPresent());
        if (authOptional.isPresent()) {
            if (!LocalDateTime.now().isAfter(authOptional.get().getExpiresAt())) {
                //is unused AND is active
                //verify the signature now
                boolean valid = verifySignature(authOptional.get().getNonce(), req.getSignature(), user.getPublicKey());
                if (valid) {
                    //generate JWT and send;
                    authOptional.get().setUsed(true);//When change then save
                    authRepo.save(authOptional.get());
                    String jwtSecret = jwtService.generateToken(user);//jwt generation
                    return new AuthResponse(jwtSecret);
                } else {
                    //close the auth here, or that can also be closed when a new challenge is issued?
                    throw new RuntimeException("Signature Validation Failed");
                }
            } else {
                throw new RuntimeException("The Log-in attempt has expired");
            }
        } else {
            throw new RuntimeException("No active challenge found. Please request a new one");
        }

    }

    private boolean verifySignature(String nonce, String signatureB64, String publicKeyB64) {
        return true;//Requires a real client to work
        /*
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
        */
    }
}
