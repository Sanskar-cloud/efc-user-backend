package com.example.efc_user.repo;

import com.example.efc_user.collections.Bookings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepo extends MongoRepository<Bookings, String> {
    List<Bookings> findByFacilityIdAndStartTimeBeforeAndEndTimeAfter(String facilityId, LocalDateTime endTime, LocalDateTime startTime);
    List<Bookings> findByUserId(String userId);}
