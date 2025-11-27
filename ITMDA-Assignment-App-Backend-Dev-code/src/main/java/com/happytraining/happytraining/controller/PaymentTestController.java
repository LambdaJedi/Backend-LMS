package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Payment;
import com.happytraining.happytraining.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test/payments")
public class PaymentTestController {

    @Autowired
    private PaymentService paymentService;

    // Test endpoint to simulate complete payment flow
    @PostMapping("/simulate-payment")
    public Map<String, Object> simulatePayment(@RequestBody Payment p) throws Exception {
        // Step 1: Create payment
        Payment created = paymentService.createPayment(p);

        // Step 2: Initiate payment (get PayFast details)
        Map<String, Object> initiated = paymentService.initiatePayment(p);

        // Step 3: Return all test information
        Map<String, Object> result = new HashMap<>();
        result.put("payment", created);
        result.put("payfast", initiated.get("payfast"));
        result.put("testInstructions", Map.of(
                "itnUrl", "POST http://localhost:8080/api/payments/payfast-itn",
                "testPayload", createTestITNPayload(created.getId(), p.getAmount()),
                "verifyUrl", "GET http://localhost:8080/api/payments/verify/" + created.getId()
        ));

        return result;
    }

    // Direct ITN testing endpoint
    @PostMapping("/simulate-itn")
    public String simulateITN(@RequestParam String paymentId,
                              @RequestParam String status,
                              @RequestParam double amount) throws Exception {
        Map<String, String> itnPayload = new HashMap<>();
        itnPayload.put("m_payment_id", paymentId);
        itnPayload.put("payment_status", status);
        itnPayload.put("amount", String.valueOf(amount));
        itnPayload.put("item_name", "Test Course Payment");
        itnPayload.put("pf_payment_id", "TEST_" + System.currentTimeMillis());

        // Convert to Object map for the service
        Map<String, Object> objectPayload = new HashMap<>(itnPayload);
        paymentService.processWebhook(objectPayload);

        return "ITN simulation completed for payment: " + paymentId + " with status: " + status;
    }

    private Map<String, String> createTestITNPayload(String paymentId, double amount) {
        Map<String, String> payload = new HashMap<>();
        payload.put("m_payment_id", paymentId);
        payload.put("payment_status", "COMPLETE");
        payload.put("amount", String.format("%.2f", amount));
        payload.put("item_name", "Course payment: test-course");
        payload.put("pf_payment_id", "PF_TEST_12345");
        payload.put("name_first", "Test");
        payload.put("name_last", "User");
        payload.put("email_address", "test@example.com");
        return payload;
    }
}
