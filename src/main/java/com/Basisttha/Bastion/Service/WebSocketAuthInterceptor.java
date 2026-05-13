package com.Basisttha.Bastion.Service;

import java.util.Collections;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.Basisttha.Bastion.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                if (jwtService.isTokenValid(jwt)) {
                    String userId = jwtService.extractUserId(jwt);
                    userRepository.findById(UUID.fromString(userId)).ifPresent(user -> {
                        UsernamePasswordAuthenticationToken auth
                                = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                        accessor.setUser(auth);
                    });
                }
            }
        }
        return message;
    }
}
