package com.example.controller;


import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        try {
            return accountService.register(account)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(400).build());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        return accountService.login(account.getUsername(), account.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()
                || message.getMessageText().length() > 255) {
            return ResponseEntity.badRequest().build();
        }
        if (message.getPostedBy() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Optional<Message> createdMessage = messageService.createMessage(message);
            if (createdMessage.isPresent()) {
                return ResponseEntity.ok(createdMessage.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable("messageId") Long messageId) {
        try {
            int rowsModified = messageService.deleteMessage(messageId);

            if (rowsModified > 0) {
                return ResponseEntity.ok(1);
            } else {
                return ResponseEntity.ok(1);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }



    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long messageId) {
        return messageService.getMessageById(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageText(@PathVariable Long messageId, @RequestBody Message newText) {
        try {
            if (newText.getMessageText().trim().isEmpty() || newText.getMessageText().length() > 255) {
                return ResponseEntity.badRequest().body("Message text is either empty or exceeds the allowable limit.");
            }
            Message updatedMessage = messageService.updateMessageText(messageId, newText);
            if (updatedMessage == null) {
                return ResponseEntity.badRequest().body("Message with ID " + messageId + " not found.");
            }
            return ResponseEntity.ok(1);        } 
       catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( e.getMessage());
        }
    }

    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByUserId(@PathVariable Long accountId) {
        return messageService.getMessagesByUserId(accountId);
    }
}
