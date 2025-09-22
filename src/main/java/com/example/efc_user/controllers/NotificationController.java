package com.example.efc_user.controllers;

import com.example.efc_user.payloads.LocationUpdate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // No timeout
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    public void sendUpdate(String facilityId, String message) {
        synchronized (emitters) {
            Iterator<SseEmitter> iterator = emitters.iterator();
            while (iterator.hasNext()) {
                try {
                    SseEmitter emitter = iterator.next();
                    emitter.send(SseEmitter.event()
                            .name("facility-update")
                            .data(Map.of("facilityId", facilityId, "message", message)));
                } catch (IOException e) {
                    iterator.remove(); // Remove failed emitters
                }
            }
        }
    }

    public void broadcastLocationUpdate(String orderId, LocationUpdate update) {
        synchronized (emitters) {
            Iterator<SseEmitter> iterator = emitters.iterator();
            while (iterator.hasNext()) {
                try {
                    SseEmitter emitter = iterator.next();
                    emitter.send(SseEmitter.event()
                            .name("location-update")
                            .data(update));
                } catch (IOException e) {
                    iterator.remove(); // Remove broken connections
                }
            }
        }
    }
}
