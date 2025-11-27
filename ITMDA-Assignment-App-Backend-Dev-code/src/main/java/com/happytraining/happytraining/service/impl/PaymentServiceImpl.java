package com.happytraining.happytraining.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.happytraining.happytraining.model.Payment;
import com.happytraining.happytraining.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final CollectionReference payments;

    @Value("${payfast.merchant-id}")
    private String merchantId;

    @Value("${payfast.merchant-key}")
    private String merchantKey;

    @Value("${payfast.passphrase:}")
    private String passphrase;

    public PaymentServiceImpl(Firestore firestore) {
        this.payments = firestore.collection("payments");
    }

    @Override
    public Payment createPayment(Payment p) throws Exception {
        var ref = payments.document();
        p.setId(ref.getId());
        p.setStatus("PENDING");

        // Use the helper method instead of direct LocalDateTime
        p.setLocalDateTime(LocalDateTime.now());

        ref.set(p).get();
        return p;
    }

    @Override
    public List<Payment> getPaymentsForUser(String userId) throws Exception {
        var querySnapshot = payments.whereEqualTo("userId", userId).get().get();
        List<Payment> paymentList = new ArrayList<>();

        for (var document : querySnapshot.getDocuments()) {
            Payment payment = document.toObject(Payment.class);
            paymentList.add(payment);
        }

        return paymentList;
    }

    @Override
    public Map<String, Object> initiatePayment(Payment p) throws Exception {
        Payment saved = createPayment(p);

        String actionUrl = "https://sandbox.payfast.co.za/eng/process";

        // Build PayFast fields
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merchant_id", merchantId);
        params.put("merchant_key", merchantKey);
        params.put("return_url", "https://your-app.com/payment/success");
        params.put("cancel_url", "https://your-app.com/payment/cancel");
        params.put("notify_url", "http://localhost:8080/api/payments/payfast-itn");

        // Fix: Use decimal point for amount
        params.put("amount", String.format("%.2f", saved.getAmount()).replace(',', '.'));
        params.put("item_name", "Course payment: " + saved.getCourseId());
        params.put("m_payment_id", saved.getId());

        // Generate signature
        String signature = generateSignature(params);
        params.put("signature", signature);

        System.out.println("✅ Payment initiated: " + saved.getId());
        System.out.println("💰 Amount formatted: " + params.get("amount"));

        Map<String, Object> result = new HashMap<>();
        result.put("payment", saved);
        result.put("payfast", Map.of(
                "action", actionUrl,
                "fields", params
        ));

        return result;
    }

    private String generateSignature(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (v != null && !v.isEmpty() && !k.equals("signature")) {
                if (sb.length() > 0) sb.append("&");
                sb.append(k).append("=").append(URLEncoder.encode(v, StandardCharsets.UTF_8));
            }
        });
        if (passphrase != null && !passphrase.isEmpty()) {
            sb.append("&passphrase=").append(URLEncoder.encode(passphrase, StandardCharsets.UTF_8));
        }
        return md5(sb.toString());
    }

    private String md5(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String reference) throws Exception {
        var docRef = payments.document(reference);
        var snap = docRef.get().get();
        if (!snap.exists()) return Map.of("error", "Payment not found");

        Payment p = snap.toObject(Payment.class);
        return Map.of("reference", reference, "status", p.getStatus(), "payment", p);
    }


    @Override
    public void processWebhook(Map<String, Object> payload) throws Exception {
        // Extract string values from the Object map
        String mPaymentId = getStringValue(payload, "m_payment_id");
        String paymentStatus = getStringValue(payload, "payment_status");
        String status = "FAILED";

        if ("COMPLETE".equalsIgnoreCase(paymentStatus)) {
            status = "COMPLETED";
        }

        // Update Firestore
        if (mPaymentId != null && !mPaymentId.isEmpty()) {
            var docRef = payments.document(mPaymentId);
            docRef.update("status", status).get();
            System.out.println("ITN received for payment: " + mPaymentId + ", status: " + status);
        } else {
            System.err.println("ITN received without m_payment_id");
        }
    }

    // Helper method to safely extract String values from Object map
    private String getStringValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        // Handle other types if needed
        return value.toString();
    }
}








