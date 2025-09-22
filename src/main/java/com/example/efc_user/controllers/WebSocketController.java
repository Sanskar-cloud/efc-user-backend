package com.example.efc_user.controllers;

import com.example.efc_user.collections.Order;
import com.example.efc_user.payloads.LocationUpdate;
import com.example.efc_user.payloads.OrderUpdateRequest;
import com.example.efc_user.services.OrderService;
import com.example.efc_user.services.OrderServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Controller
public class WebSocketController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private KafkaTemplate<String, LocationUpdate> kafkaTemplate;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    SimpMessagingTemplate simpmessagingTemplate;



    // Send order details when a new order is created
    @SendTo("/topic/orders")
    public Order sendOrder(Order order) {
        return order;
    }

    // Receive order updates from frontend and broadcast them
    @MessageMapping("/update-order")
    @SendTo("/topic/order-updates")
    public Order updateOrder(OrderUpdateRequest request) {
        Order updatedOrder = orderService.updateOrderStatus(request.getId(), request.getStatus());
        return updatedOrder;
    }

    @MessageMapping("/location-update")
    public void handleLocationUpdate(LocationUpdate update) throws JsonProcessingException {
       processLocationUpdate(update);
    }

    public void processLocationUpdate(LocationUpdate update) throws JsonProcessingException {
        // Store in Redis
        redisTemplate.opsForValue().set("location:" + update.getOrderId(),
                objectMapper.writeValueAsString(update), 10, TimeUnit.MINUTES);

//        // Send to Kafka (if needed)
//        kafkaTemplate.send("delivery_location_update", update.getOrderId(), update);

        // Emit to users via SSE
     notificationController.broadcastLocationUpdate(update.getOrderId(), update);

        // Or emit via WebSocket if you also want that
        simpmessagingTemplate.convertAndSend("/topic/location/" + update.getOrderId(), update);
    }
}

