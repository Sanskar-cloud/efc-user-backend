package com.example.efc_user.config;

import com.example.efc_user.payloads.LocationUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class LocationUpdateConsumer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Map to hold SSE emitters for active user connections
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Method to allow clients to subscribe to location updates for a specific order.
     */
    public SseEmitter trackOrder(String orderId) {
        SseEmitter emitter = new SseEmitter(0L); // No timeout
        emitters.put(orderId, emitter);

        // Check if there is a latest location in Redis for the order
        try {
            String redisKey = "location:" + orderId;
            String locationData = redisTemplate.opsForValue().get(redisKey);
            if (locationData != null) {
                // If location data exists in Redis, send it to the user immediately
                LocationUpdate update = mapper.readValue(locationData, LocationUpdate.class);
                emitter.send(update);
            }
        } catch (Exception e) {
            log.error("Error retrieving or sending initial location update for order {}: {}", orderId, e.getMessage());
        }

        // Remove emitter on completion or timeout
        emitter.onCompletion(() -> emitters.remove(orderId));
        emitter.onTimeout(() -> emitters.remove(orderId));

        return emitter;
    }

    /**
     * Kafka listener to process location updates and notify connected clients via SSE.
     */
    @KafkaListener(
            topics = "delivery_location_update",
//            groupId = "live-location-group",
            containerFactory = "locationUpdateKafkaListenerContainerFactory"
    )
    public void consumeLocationUpdate(LocationUpdate update) {
        log.info("Received location update: {}", update);

        String orderId = update.getOrderId();

        try {
            // Check if there is an active SSE emitter for the given order ID
            if (emitters.containsKey(orderId)) {
                SseEmitter emitter = emitters.get(orderId);

                // Send real-time location update to SSE client
                emitter.send(update);

                log.info("Sent location update to client for orderId {}", orderId);
            }

            // Update Redis with the latest location update for the order
            String redisKey = "location:" + orderId;
            redisTemplate.opsForValue().set(redisKey, mapper.writeValueAsString(update));
        } catch (Exception e) {
            log.error("Error processing location update for order {}: {}", orderId, e.getMessage());
        }
    }
}


