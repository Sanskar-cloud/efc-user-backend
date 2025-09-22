package com.example.efc_user.repo;

import com.example.efc_user.collections.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
}

