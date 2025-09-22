package com.example.efc_user.repo;

import com.example.efc_user.collections.RazorpayTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface RazorpayTransactionRepo extends MongoRepository<RazorpayTransaction,String> {
    Optional<RazorpayTransaction> findByRazorpayOrderId(String orderId);}
