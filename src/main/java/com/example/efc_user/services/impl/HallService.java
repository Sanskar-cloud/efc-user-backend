package com.example.efc_user.services.impl;

import com.example.efc_user.collections.Hall;
import com.example.efc_user.payloads.CreateHallRequest;
import com.example.efc_user.payloads.HallDto;
import com.example.efc_user.repo.HallRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HallService {

    @Autowired
    private HallRepo hallRepository;

    public HallDto createHall(CreateHallRequest hallDto) {
        // Convert HallDto to Hall entity
        Hall hall = new Hall();
        hall.setName(hallDto.getName());
        hall.setCapacity(hallDto.getCapacity());

        // Set default values for remaining fields
        hall.setBooked(false);
        hall.setOfflineStartTime(null); // No offline time initially
        hall.setOfflineEndTime(null);

        // Save Hall to repository
        Hall savedHall = hallRepository.save(hall);

        // Convert Hall entity back to HallDto
        return convertToDto(savedHall);
    }

    private HallDto convertToDto(Hall hall) {
        HallDto hallDto = new HallDto();
        hallDto.setId(hall.getId());
        hallDto.setName(hall.getName());
        hallDto.setCapacity(hall.getCapacity());
        hallDto.setBooked(hall.isBooked());
        hallDto.setOfflineStartTime(hall.getOfflineStartTime());
        hallDto.setOfflineEndTime(hall.getOfflineEndTime());
        return hallDto;
    }
}
