package com.example.efc_user.controllers;

import com.example.efc_user.collections.RazorpayTransaction;
import com.example.efc_user.collections.TransactionStatus;
import com.example.efc_user.repo.RazorpayTransactionRepo;
import com.example.efc_user.services.OrderService;
import com.example.efc_user.services.OrderServiceImpl;
import com.example.efc_user.services.impl.RazorpayService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    RazorpayService razorpayService;
    @Autowired
    RazorpayTransactionRepo razorpayTransactionRepo;
    @Autowired
    OrderServiceImpl orderService;

    @Value("${razorpay.key_secret_webhook}")
    private String secret;
    @Value("${stripe.webhook_secret}")
    private String stripeWebhookSecret;

//    @PostMapping("/create-payment")
//    public ResponseEntity<?> createIntent(Principal principal) throws Exception {
//
//
//        String clientSecret = razorpayService.createPaymentIntent(principal);
//
//        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
//    }


    @PostMapping("/create-payment")
    public ResponseEntity<?> createPaymentLink(Principal principal) throws RazorpayException {
        Map<String, String> response = razorpayService.createPaymentLink(principal);
        return ResponseEntity.ok(response);
    }


//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleWebhook(
//            @RequestHeader("Stripe-Signature") String sigHeader,
//            @RequestBody String payload) {
//
//        try {
//            Event event = razorpayService.constructEvent(payload, sigHeader, stripeWebhookSecret);
//            if ("payment_intent.succeeded".equals(event.getType())) {
//                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
//                        .getObject()
//                        .orElseThrow();
//
//                String clientSecret = intent.getClientSecret();
//                RazorpayTransaction tx = razorpayTransactionRepo.findByRazorpayOrderId(clientSecret)
//                        .orElseThrow();
//
//                tx.setStatus(TransactionStatus.SUCCESS);
//               razorpayTransactionRepo.save(tx);
//            }
//            if ("payment_intent.payment_failed".equals(event.getType())) {
//                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
//                        .getObject()
//                        .orElseThrow();
//
//                String clientSecret = intent.getClientSecret();
//                RazorpayTransaction tx = razorpayTransactionRepo.findByRazorpayOrderId(clientSecret)
//                        .orElseThrow();
//
//                tx.setStatus(TransactionStatus.FAILED);
//                tx.setFailureReason(intent.getLastPaymentError().getMessage());
//                razorpayTransactionRepo.save(tx);
//            }
//
//            return ResponseEntity.ok("Handled");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
//        }
//    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payloadJson
    ) throws Exception {

        String expectedSignature = hmacSHA256(payloadJson, secret);
        if (!expectedSignature.equals(signature)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payloadJson);
        String event = root.path("event").asText();

        if (!"payment_link.paid".equalsIgnoreCase(event)) {
            return ResponseEntity.ok("Event ignored: " + event);
        }

        // Extract info
        JsonNode payment = root.path("payload").path("payment").path("entity");
        JsonNode paymentLink = root.path("payload").path("payment_link").path("entity");

        String razorpayOrderId = payment.path("order_id").asText();
        String paymentId = payment.path("id").asText();
        int amount = payment.path("amount").asInt();
        String contact = payment.path("contact").asText();
        String email = payment.path("email").asText();
        String status = payment.path("status").asText();
        String paymentLinkId=paymentLink.path("id").asText();
        String referenceId = paymentLink.path("reference_id").asText();
        String failureReason = payment.path("error_reason").asText(null);

        Optional<RazorpayTransaction> optTx = razorpayService.getTransactionByOrderId(paymentLinkId);
        if (optTx.isEmpty()) return ResponseEntity.status(404).body("Order not found");

        RazorpayTransaction tx = optTx.get();
        if (tx.getStatus() != TransactionStatus.PENDING) {
            return ResponseEntity.ok("Already processed");
        }

        if ("captured".equalsIgnoreCase(status)) {
            tx.setStatus(TransactionStatus.SUCCESS);


        } else {
            tx.setStatus(TransactionStatus.FAILED);
            tx.setFailureReason(failureReason != null ? failureReason : "Payment failed");


        }

        razorpayTransactionRepo.save(tx);
        return ResponseEntity.ok("Processed payment_link.paid");
    }


    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getStatus(@PathVariable String orderId) {
        return razorpayService.getTransactionByOrderId(orderId)
                .map(tx -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", tx.getStatus());
                    response.put("amount", tx.getAmount());
                    response.put("failureReason", tx.getFailureReason() != null ? tx.getFailureReason() : "N/A");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    private String hmacSHA256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return Hex.encodeHexString(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}
