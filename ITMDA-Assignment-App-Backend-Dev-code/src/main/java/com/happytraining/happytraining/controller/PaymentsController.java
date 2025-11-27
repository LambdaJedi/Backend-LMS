package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Payment;
import com.happytraining.happytraining.service.PaymentService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/payments")
public class PaymentsController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment p) throws Exception {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();

        p.setUserId(uid);
        Payment created = paymentService.createPayment(p);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsForUser(@PathVariable String userId) throws Exception {
        return ResponseEntity.ok(paymentService.getPaymentsForUser(userId));
    }

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody Payment p) throws Exception {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();

        p.setUserId(uid);
        Map<String, Object> response = paymentService.initiatePayment(p);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<Map<String, Object>> verifyPayment(@PathVariable String reference) throws Exception {
        Map<String, Object> response = paymentService.verifyPayment(reference);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payfast-itn")
    public ResponseEntity<String> payfastITN(@RequestParam Map<String, String> payload) throws Exception {
        // Convert Map<String, String> to Map<String, Object>
        Map<String, Object> objectPayload = new HashMap<>(payload);
        paymentService.processWebhook(objectPayload);
        return ResponseEntity.ok("ITN received");
    }
}







