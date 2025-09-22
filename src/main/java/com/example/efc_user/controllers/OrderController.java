package com.example.efc_user.controllers;

import com.example.efc_user.collections.Order;
import com.example.efc_user.payloads.LocationUpdate;
import com.example.efc_user.payloads.OrderDto;
import com.example.efc_user.payloads.OrderFromCartRequest;
import com.example.efc_user.payloads.OrderRequest;
import com.example.efc_user.response.BaseApiResponse;
import com.example.efc_user.response.LoginDeliveryResponse;
import com.example.efc_user.security.CustomUserDetailService;
import com.example.efc_user.services.OrderService;
import com.example.efc_user.services.OrderServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")

public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
 private OrderServiceImpl orderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/from-cart")
    public ResponseEntity<OrderDto> createOrderFromCart(Principal principal, @RequestBody OrderFromCartRequest request) {
        try {

            // Get the authenticated user's ID

            OrderDto createdOrder = orderService.createOrderFromCart(principal, request);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            log.info(e.getMessage()+"dsfggdfvdf");
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
    @GetMapping("/orders/{orderId}/latest-location")
    public ResponseEntity<LocationUpdate> getLatest(@PathVariable String orderId) throws JsonProcessingException {
        String json = redisTemplate.opsForValue().get("location:" + orderId);
        if (json != null) {
            LocationUpdate update = objectMapper.readValue(json, LocationUpdate.class);
            return ResponseEntity.ok(update);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
