package com.example.efc_user.config;

import com.example.efc_user.payloads.AdminUpdateRequest;
import com.example.efc_user.services.impl.BookingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
class HallUpdateConsumer {
    @Autowired
    private BookingServiceImpl bookingService;

    @KafkaListener(topics = "admin_updates",  containerFactory = "hallUpdateKafkaListenerContainerFactory")
    public void consumeAdminUpdate(AdminUpdateRequest updateRequest) {
        bookingService.updateHallOfflineStatus(updateRequest);
    }
}
