package com.example.efc_user.controllers;

import com.example.efc_user.collections.Bookings;
import com.example.efc_user.payloads.BookingDto;
import com.example.efc_user.payloads.CreateBookingRequest;
import com.example.efc_user.services.impl.BookingServiceImpl;
import com.example.efc_user.subscribers.RedisMessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/bookings")
class BookingControllers {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private RedisMessageSubscriber redisSubscriber;

    @PostMapping
    public ResponseEntity<?> createBooking(Principal principal, @RequestBody CreateBookingRequest booking) {
        try {
            BookingDto savedBooking = bookingService.createBooking(principal,booking);
            return ResponseEntity.ok(savedBooking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking canceled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//
//    @GetMapping("/subscribe")
//    public SseEmitter subscribe() {
//        SseEmitter emitter = new SseEmitter(0L); // No timeout
//        emitters.add(emitter);
//
//        emitter.onCompletion(() -> emitters.remove(emitter));
//        emitter.onTimeout(() -> emitters.remove(emitter));
//        emitter.onError(e -> emitters.remove(emitter));
//
//        return emitter;
//    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable String bookingId) {
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        try {
            List<BookingDto> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            // Log the exception (for debugging purposes)
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.emptyList());  // Return empty list or an appropriate message
        }
    }


    // Get all bookings by the principal (user)
    @GetMapping("/user")
    public ResponseEntity<List<BookingDto>> getBookingsByPrincipal(Principal principal) {
        try {
            List<BookingDto> bookings = bookingService.getBookingsByUser(principal.getName());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

