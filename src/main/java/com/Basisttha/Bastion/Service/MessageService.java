package com.Basisttha.Bastion.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.Basisttha.Bastion.DTO.MessageResponse;
import com.Basisttha.Bastion.DTO.SendMessageRequest;
import com.Basisttha.Bastion.Exception.UserNotFoundException;
import com.Basisttha.Bastion.Model.DeliveryStatus;
import com.Basisttha.Bastion.Model.Message;
import com.Basisttha.Bastion.Model.User;
import com.Basisttha.Bastion.Repository.MessageRepository;
import com.Basisttha.Bastion.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepo;
    private final MessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageResponse sendMessage(UUID senderId, SendMessageRequest req) {
        //Part 1. Find if the sender and receiver exist
        User sender = userRepo.findById(senderId).orElseThrow(() -> new UserNotFoundException("This user does not exist"));
        User receiver = userRepo.findById(req.getRecipientId()).orElseThrow(() -> new UserNotFoundException("This user does not exist"));

        Message message = Message.builder().sender(sender).recipient(receiver).cipherText(req.getCipherText()).nonce(req.getNonce()).build();
        Message saved = messageRepo.save(message);
        messagingTemplate.convertAndSend("/topic/messages/" + receiver.getId(), toResponse(saved));

        saved.setDeliveryStatus(DeliveryStatus.DELIVERED);
        saved.setDeliveredAt(LocalDateTime.now());
        messageRepo.save(message);

        return toResponse(saved);
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getCipherText(),
                message.getNonce(),
                message.getDeliveryStatus().toString(),
                message.getCreatedAt().toString()
        );
    }

    public List<MessageResponse> getConversation(UUID currentUserId, UUID contactId) {
        List<Message> messages = messageRepo.findMConversationBetween(currentUserId, contactId);

        messageRepo.markMessagesAsRead(currentUserId, contactId);

        return messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
