package com.example.efc_user.services.impl;

import com.example.efc_user.collections.Bookings;
import com.example.efc_user.collections.Facility;
import com.example.efc_user.collections.Hall;
import com.example.efc_user.collections.User;
import com.example.efc_user.payloads.*;
import com.example.efc_user.repo.BookingRepo;
import com.example.efc_user.repo.FacilityRepo;
import com.example.efc_user.repo.HallRepo;
import com.example.efc_user.repo.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl {
    @Autowired
    private BookingRepo bookingRepository;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private HallRepo hallRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ChannelTopic redisChannelTopic;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    private KafkaTemplate<String, BookingDto> onlineBookingUpdateKafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();



    public boolean isHallAvailable(String facilityId, LocalDateTime startTime, LocalDateTime endTime) {
        Facility facility = facilityRepo.findById(facilityId).orElseThrow(() -> new RuntimeException("Hall not found"));

        // Null check for offline start and end times
        LocalDateTime offlineStartTime = facility.getOfflineStartTime();
        LocalDateTime offlineEndTime = facility.getOfflineEndTime();

        // Check if hall is booked and offline times exist and overlap with requested booking time
        if (facility.isBooked() &&
                offlineStartTime != null && offlineEndTime != null &&
                offlineStartTime.isBefore(endTime) && offlineEndTime.isAfter(startTime)) {
            return false;
        }

        // Check for conflicting bookings
        List<Bookings> conflictingBookings = bookingRepository.findByFacilityIdAndStartTimeBeforeAndEndTimeAfter(facilityId, endTime, startTime);
        return conflictingBookings.isEmpty();
    }


    public BookingDto createBooking(Principal principal, CreateBookingRequest booking) throws JsonProcessingException {
        if (!isHallAvailable(booking.getFacilityId(), booking.getStartTime(), booking.getEndTime())) {
            throw new RuntimeException("Hall not available for the selected time");
        }

        Bookings bookings = new Bookings();
        List<OrderItem> orderItems = booking.getItems().stream()
                .map(itemDto -> this.modelMapper.map(itemDto, OrderItem.class))
                .collect(Collectors.toList());

        // Fetching the Hall entity by ID
        Facility hall = facilityRepo.findById(booking.getFacilityId()).orElseThrow();
        hall.setBooked(true);
        Facility savedHall = facilityRepo.save(hall);
        FacilityDto facilityDto = this.modelMapper.map(savedHall, FacilityDto.class);

        // Saving the booking
        bookings.setFacilityId(savedHall.getId());  // Saving the hallId as a String
        bookings.setStartTime(booking.getStartTime());
        bookings.setEndTime(booking.getEndTime());
        bookings.setItems(orderItems);
        bookings.setStatus("confirmation pending");

        User user = userRepo.findByEmail(principal.getName()).orElseThrow();
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        bookings.setUserId(userDto.getId());


        Bookings savedBooking = bookingRepository.save(bookings);



        Map<String, Object> updateMessage = Map.of(
                "facilityId", savedBooking.getFacilityId(),
                "message", String.format("Facility %s is now booked", hall.getName()),
                "userId",savedBooking.getUserId()
        );

        // Publish the update to the Redis channel
        redisTemplate.convertAndSend("facility-updates", updateMessage);
//        redisTemplate.convertAndSend(
//                redisChannelTopic.getTopic(),
//                mapper.writeValueAsString(Map.of("facilityId", savedBooking.getFacilityId(), "isAvailable", false, "userId", savedBooking.getUserId()))
//        );


        BookingDto bookingDto = this.modelMapper.map(savedBooking, BookingDto.class);
        bookingDto.setFacilityId(facilityDto);
        bookingDto.setUserId(userDto);
        onlineBookingUpdateKafkaTemplate.send("online_booking_update", bookingDto);
        

        return bookingDto;
    }


    public void cancelBooking(String bookingId) throws JsonProcessingException {
        Bookings booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Facility facility=facilityRepo.findById(booking.getFacilityId()).orElseThrow();
        facility.setBooked(false);
        facilityRepo.save(facility);

        bookingRepository.delete(booking);

        redisTemplate.convertAndSend(
                redisChannelTopic.getTopic(),
                mapper.writeValueAsString(Map.of("hallId", booking.getFacilityId(), "isAvailable", true))
        );
    }
    public void updateHallOfflineStatus(AdminUpdateRequest updateRequest) {
        try {


            Facility facility = facilityRepo.findById(updateRequest.getFacilityId())
                    .orElseThrow(() -> new RuntimeException("Hall not found"));
            facility.setBooked(updateRequest.isBooked());
            facility.setOfflineStartTime(updateRequest.getStartTime());
            facility.setOfflineEndTime(updateRequest.getEndTime());

            facilityRepo.save(facility);
            redisTemplate.convertAndSend(redisChannelTopic.getTopic(), updateRequest.getFacilityId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BookingDto getBookingById(String bookingId) {
        Bookings booking = bookingRepository.findById(bookingId).orElseThrow();


        Facility facility = facilityRepo.findById(booking.getFacilityId()).orElseThrow();
        User user =userRepo.findById(booking.getUserId()).orElseThrow();



        BookingDto bookingDto=this.modelMapper.map(booking,BookingDto.class);
        bookingDto.setFacilityId(this.modelMapper.map(facility,FacilityDto.class));
        bookingDto.setUserId(this.modelMapper.map(user,UserDto.class));
        return bookingDto;
    }

    public List<BookingDto> getAllBookings() {
        List<Bookings> bookings = bookingRepository.findAll();

        return bookings.stream()
                .map(booking -> {
                    // Map the Booking entity to BookingDto
                    BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);

                    // Fetch Hall and User by their respective IDs and map to DTOs
                    Facility facility = facilityRepo.findById(booking.getFacilityId()).orElseThrow();
                    User user = userRepo.findById(booking.getUserId()).orElseThrow();

                    // Set Hall and User DTOs in the BookingDto
                    bookingDto.setFacilityId(modelMapper.map(facility, FacilityDto.class));
                    bookingDto.setUserId(modelMapper.map(user, UserDto.class));

                    return bookingDto;
                })
                .collect(Collectors.toList());
    }


    public List<BookingDto> getBookingsByUser(String email) {
        User user=userRepo.findByEmail(email).orElseThrow();


        List<Bookings> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(booking -> {
                    // Map the Booking entity to BookingDto
                    BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);

                    // Fetch Hall and User by their respective IDs and map to DTOs
                    Facility facility = facilityRepo.findById(booking.getFacilityId()).orElseThrow();
                    User userr = userRepo.findById(booking.getUserId()).orElseThrow();

                    // Set Hall and User DTOs in the BookingDto
                    bookingDto.setFacilityId(modelMapper.map(facility, FacilityDto.class));
                    bookingDto.setUserId(modelMapper.map(userr, UserDto.class));

                    return bookingDto;
                })
                .collect(Collectors.toList());
    }



}
