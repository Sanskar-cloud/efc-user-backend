package com.example.efc_user.controllers;

import com.example.efc_user.collections.Order;
//import com.example.efc_user.config.LocationUpdateConsumer;
import com.example.efc_user.payloads.LocationUpdate;
import com.example.efc_user.payloads.OrderDeliverBoyDto;
import com.example.efc_user.payloads.ReviewRequest;
import com.example.efc_user.response.BaseApiResponse;
import com.example.efc_user.response.LoginResponse;
import com.example.efc_user.services.DeliveryBoyService;
import com.example.efc_user.services.OrderServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    DeliveryBoyService deliveryBoyService;
    @Autowired
    KafkaTemplate<String,LocationUpdate>kafkaTemplate;
//    @Autowired
//    LocationUpdateConsumer locationUpdateConsumer;


    @GetMapping("/orders/pending")
    public ResponseEntity<BaseApiResponse<List<OrderDeliverBoyDto>>> getPendingOrders() {
        List<OrderDeliverBoyDto> orders=orderService.getPendingOrders();
        return  ResponseEntity.ok(
                BaseApiResponse.<List<OrderDeliverBoyDto>>builder()
                        .success(true)
                        .message("Pending Orders")
                        .data(orders)
                        .build());
    }
    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<BaseApiResponse<Order>> acceptOrder(@PathVariable String orderId, Principal principal) {
        Order order= orderService.acceptOrder(orderId,principal);
        return  ResponseEntity.ok(
                BaseApiResponse.<Order>builder()
                        .success(true)
                        .message("Accepted Order")
                        .data(order)
                        .build());




    }
    @PostMapping("/review/{deliveryBoyId}")
    public ResponseEntity<BaseApiResponse<String>> addReview(
            @PathVariable String deliveryBoyId,
            Principal principal,
            @RequestBody ReviewRequest reviewRequest) {
        try {
            deliveryBoyService.addReview(deliveryBoyId, principal, reviewRequest.getRating(), reviewRequest.getReviewText());
            return ResponseEntity.ok(
                    BaseApiResponse.<String>builder()
                            .success(true)
                            .message("Thanks for your time!!")
                            .data(null)
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(
                    BaseApiResponse.<String>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdate update) {
        try {
            kafkaTemplate.send("delivery_location_update",update.getOrderId(),update);
            return ResponseEntity.ok("Location update sent.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending location update: " + e.getMessage());
        }

    }
//    @GetMapping("/{orderId}/live-location")
//    public SseEmitter trackOrder(@PathVariable String orderId) {
//        return locationUpdateConsumer.trackOrder(orderId);
//    }


}





