package com.example.efc_user.repo;


import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.DeliveryBoyReview;
import com.example.efc_user.collections.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryBoyReviewRepo extends MongoRepository<DeliveryBoyReview, String> {


}

