package com.example.efc_user.services.impl;

import com.example.efc_user.collections.Cart;
import com.example.efc_user.collections.RazorpayTransaction;
import com.example.efc_user.collections.TransactionStatus;
import com.example.efc_user.collections.User;
import com.example.efc_user.payloads.CartDto;
import com.example.efc_user.repo.RazorpayTransactionRepo;
import com.example.efc_user.repo.UserRepo;
import com.razorpay.Order;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
public class RazorpayService {
@Autowired
private RazorpayClient razorpayClient;

@Autowired
private RazorpayTransactionRepo transactionRepository;
@Autowired
private UserRepo userRepo;
@Autowired
private CartService cartService;

    @Value("${stripe.secret_key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createPaymentIntent(Principal principal) throws StripeException {
        User user=userRepo.findByEmail(principal.getName()).orElseThrow();
        CartDto cart=cartService.getCart(principal);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        double amount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int)(amount * 100)); // amount in cents
        params.put("currency", "INR");
        params.put("metadata", Map.of("userId", user.getId()));

        PaymentIntent intent = PaymentIntent.create(params);

        RazorpayTransaction tx = new RazorpayTransaction();
        tx.setRazorpayOrderId(intent.getClientSecret());
        tx.setUserId(user.getId());
        tx.setAmount(amount);
        tx.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(tx);


        return intent.getClientSecret(); // send this to frontend
    }

    public Event constructEvent(String payload, String sigHeader, String endpointSecret) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, endpointSecret);
    }


//
//public String createOrder(Principal principal) throws RazorpayException {
//    User user=userRepo.findByEmail(principal.getName()).orElseThrow();
//    CartDto cart=cartService.getCart(principal);
//    if (cart.getItems().isEmpty()) {
//        throw new RuntimeException("Cart is empty");
//    }
//
//    double totalAmount = cart.getItems().stream()
//            .mapToDouble(item -> item.getPrice() * item.getQuantity())
//            .sum();
//
//
//
//
//    JSONObject orderRequest = new JSONObject();
//    orderRequest.put("amount", (int) (totalAmount * 100)); // Amount in paise
//    orderRequest.put("currency", "INR");
//    orderRequest.put("payment_capture", 1);
//    orderRequest.put("receipt", "rcpt_no" + 1);
//    JSONObject metadata = new JSONObject();
//    metadata.put("userId", user.getId());
//    orderRequest.put("notes", metadata);
//
//
//    Order order = razorpayClient.orders.create(orderRequest);
//
//    RazorpayTransaction tx = new RazorpayTransaction();
//    tx.setRazorpayOrderId(order.get("id"));
//    tx.setUserId(user.getId());
//    tx.setAmount(totalAmount);
//    tx.setStatus(TransactionStatus.PENDING);
//    transactionRepository.save(tx);
//
//
//
//
//    return order.get("id");
//}

    public Map<String, String> createPaymentLink(Principal principal) throws RazorpayException {
        User user = userRepo.findByEmail(principal.getName()).orElseThrow();
        CartDto cart = cartService.getCart(principal);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", (int) (totalAmount * 100)); // Amount in paise
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("accept_partial", false);
        paymentLinkRequest.put("description", "Payment for Cart");
        paymentLinkRequest.put("reference_id", UUID.randomUUID().toString());

        JSONObject customer = new JSONObject();
        customer.put("name", user.getName());
        customer.put("email", user.getEmail());
//        customer.put("contact", user.getPhone());
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("reminder_enable", true);
//        paymentLinkRequest.put("callback_url", "https://efc-app-1.onrender.com/api/v1/payments/webhook");
//        paymentLinkRequest.put("callback_method", "get");

        PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

        String paymentLinkId = paymentLink.get("id");
        String shortUrl = paymentLink.get("short_url");

        // Save transaction
        RazorpayTransaction tx = new RazorpayTransaction();
        tx.setRazorpayOrderId(paymentLinkId);
        log.info("Created Payment Link ID: {}", paymentLinkId);
        tx.setUserId(user.getId());
        tx.setAmount(totalAmount);
        tx.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(tx);

        // Return both
        return Map.of(
                "paymentLinkId", paymentLinkId,
                "shortUrl", shortUrl
        );
    }




    public Optional<RazorpayTransaction> getTransactionByOrderId(String orderId) {
    return transactionRepository.findByRazorpayOrderId(orderId);
}}
