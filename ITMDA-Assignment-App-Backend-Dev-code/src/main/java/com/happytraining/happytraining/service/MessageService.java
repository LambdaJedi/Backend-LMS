package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Message;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface MessageService {
    Message sendMessage(Message message) throws ExecutionException, InterruptedException;
    List<Message> getMessages(String conversationId, int limit) throws ExecutionException, InterruptedException;
    List<String> getConversations(String userId) throws ExecutionException, InterruptedException;
}

