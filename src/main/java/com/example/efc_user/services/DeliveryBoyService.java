package com.example.efc_user.services;

import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.DeliveryBoyReview;
import com.example.efc_user.collections.User;
import com.example.efc_user.repo.DeliveryBoyRepo;
import com.example.efc_user.repo.DeliveryBoyReviewRepo;
import com.example.efc_user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class DeliveryBoyService {

    @Autowired
    private DeliveryBoyRepo deliveryBoyRepository;

    @Autowired
    private DeliveryBoyReviewRepo deliveryBoyReviewRepository;
    @Autowired
    UserRepo userRepo;

    @Transactional
    public void addReview(String deliveryBoyId, Principal principal, int rating, String reviewText) {
        // Create a new review
        User user=userRepo.findByEmail(principal.getName()) .orElseThrow(() -> new IllegalArgumentException("Delivery boy with email " + principal.getName() + " not found"));
        DeliveryBoyReview review = new DeliveryBoyReview();
        review.setDeliveryBoyId(deliveryBoyId);
        review.setCustomerId(user.getId());
        review.setRating(rating);
        review.setReview(reviewText);
        review.setReviewDate(LocalDateTime.now());

        // Save the review
        deliveryBoyReviewRepository.save(review);

        // Update the delivery boy's average rating
        DeliveryBoy deliveryBoy = deliveryBoyRepository.findById(deliveryBoyId).orElseThrow(() -> new RuntimeException("Delivery boy not found"));
        deliveryBoy.updateRating(rating);

        // Save the updated delivery boy with the new average rating
        deliveryBoyRepository.save(deliveryBoy);
    }
}

