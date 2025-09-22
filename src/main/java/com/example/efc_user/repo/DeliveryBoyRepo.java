package com.example.efc_user.repo;

import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.Order;
import com.example.efc_user.collections.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryBoyRepo extends MongoRepository<DeliveryBoy, String> {

    Optional<DeliveryBoy> findByEmail(String email);



    boolean existsByEmail(String email);
//
//    List<User> findByNameContainingIgnoreCase(String name);
//
//    List<User> findByPhoneContaining(String phone);
//
//    @Query("{ '_id': { '$in': ?0 } }")
//    List<User> findAllByIdd(List<String> playerIds);
}

