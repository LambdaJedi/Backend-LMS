package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Message;
import com.happytraining.happytraining.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        try {
            Message sent = messageService.sendMessage(message);
            return ResponseEntity.status(201).body(sent);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable String conversationId,
                                                         @RequestParam(defaultValue = "50") int limit) {
        try {
            List<Message> msgs = messageService.getMessages(conversationId, limit);
            return ResponseEntity.ok(msgs);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<String>> getConversationsForUser(@PathVariable String userId) {
        try {
            List<String> convs = messageService.getConversations(userId);
            return ResponseEntity.ok(convs);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

