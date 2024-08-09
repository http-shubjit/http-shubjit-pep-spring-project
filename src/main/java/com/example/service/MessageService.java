package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Optional<Message> createMessage(Message message) {
        if (message == null ||
                message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
                message.getMessageText().length() > 255 ||
                message.getPostedBy() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(messageRepository.save(message));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }

    public Message updateMessageText(Long id, Message message) {
        if (messageRepository.existsById(id)) {
            Message updatMessage = messageRepository.getById(id);
            updatMessage.setMessageText(message.getMessageText());
            return messageRepository.save(updatMessage);
            
    }
        return null;
    }

   
    public int deleteMessage(Long messageId) {
    
            if (messageRepository.existsById(messageId)) {
                messageRepository.deleteById(messageId);
                return 1; 
            } else {
                return 0; 
            }
        
    }

    public List<Message> getMessagesByUserId(Long userId) {
        return messageRepository.findByPostedBy(userId);
    }
}
