package com.example.efc_user.repo;



import com.example.efc_user.collections.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OarderRepo extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByDeliveryBoyId(String deliveryBoyId);
    List<Order> findByDeliveryBoyIsNull();
}

