package com.example.efc_user.subscribers;

import com.example.efc_user.controllers.NotificationController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisMessageSubscriber  {
    private final NotificationController notificationController;


    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public RedisMessageSubscriber(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void addEmitter(String facilityId, SseEmitter emitter) {
        emitters.put(String.valueOf(Integer.parseInt(facilityId)), emitter);
    }

    public void removeEmitter(String facilityId) {
        emitters.remove(facilityId);
    }

    public void onMessage(String message) {
        try {
            var data = new ObjectMapper().readValue(message, Map.class);
            String facilityId = (String) data.get("facilityId");
            String statusMessage = (String) data.get("message");
            notificationController.sendUpdate(facilityId, statusMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
