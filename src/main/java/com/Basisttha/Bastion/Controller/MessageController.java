package com.Basisttha.Bastion.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Basisttha.Bastion.DTO.MessageResponse;
import com.Basisttha.Bastion.DTO.SendMessageRequest;
import com.Basisttha.Bastion.Model.User;
import com.Basisttha.Bastion.Service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> send(@RequestBody SendMessageRequest req){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(messageService.sendMessage(currentUser.getId(), req));
    }

    @GetMapping("/conversation/{contactId}")
    public ResponseEntity<List<MessageResponse>> getConversation(@PathVariable UUID contactId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(messageService.getConversation(user.getId(), contactId));
    }
}