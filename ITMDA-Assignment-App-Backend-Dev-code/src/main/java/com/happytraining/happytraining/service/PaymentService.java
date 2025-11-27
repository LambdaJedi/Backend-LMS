package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Payment;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    Payment createPayment(Payment p) throws Exception;
    List<Payment> getPaymentsForUser(String userId) throws Exception;
    Map<String, Object> initiatePayment(Payment p) throws Exception;
    Map<String, Object> verifyPayment(String reference) throws Exception;
    void processWebhook(Map<String, Object> payload) throws Exception; // This should be Object, not String
}


