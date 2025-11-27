package com.happytraining.happytraining.service.impl;

import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.Message;
import com.happytraining.happytraining.service.MessageService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private final CollectionReference messages;
    private final CollectionReference conversations;

    public MessageServiceImpl(Firestore firestore) {
        this.messages = firestore.collection("messages");
        this.conversations = firestore.collection("conversations");
    }

    @Override
    public Message sendMessage(Message message) throws ExecutionException, InterruptedException {
        if (message.getConversationId() == null || message.getConversationId().isEmpty()) {
            DocumentReference convRef = conversations.document();
            conversations.document(convRef.getId()).set(Map.of(
                    "createdAt", FieldValue.serverTimestamp(),
                    "participants", message.getParticipants() != null ? message.getParticipants() : List.of(message.getFrom(), message.getTo())
            )).get();
            message.setConversationId(convRef.getId());
        }
        DocumentReference ref = messages.document();
        message.setId(ref.getId());
        message.setCreatedAt(new java.util.Date());
        ref.set(message).get();
        conversations.document(message.getConversationId()).update("lastMessage", message).get();
        return message;
    }

    @Override
    public List<Message> getMessages(String conversationId, int limit) throws ExecutionException, InterruptedException {
        Query q = messages.whereEqualTo("conversationId", conversationId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        List<Message> msgs = q.get().get().toObjects(Message.class);
        java.util.Collections.reverse(msgs);
        return msgs;
    }

    @Override
    public List<String> getConversations(String userId) throws ExecutionException, InterruptedException {
        Query q = conversations.whereArrayContains("participants", userId);
        List<QueryDocumentSnapshot> docs = q.get().get().getDocuments();
        return docs.stream().map(DocumentSnapshot::getId).collect(java.util.stream.Collectors.toList());
    }
}

