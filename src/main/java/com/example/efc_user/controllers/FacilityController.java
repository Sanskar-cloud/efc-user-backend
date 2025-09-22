package com.example.efc_user.controllers;

import com.example.efc_user.payloads.CreateFacilityRequest;
import com.example.efc_user.payloads.FacilityDto;
import com.example.efc_user.services.impl.FacilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {

    private static final Logger log = LoggerFactory.getLogger(FacilityController.class);
    @Autowired
    private FacilityService facilityService;

    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("capacity") String capacity,
            @RequestParam("description") String description,
            @RequestParam("pictures") MultipartFile[] pictures) { // Accept multiple files
        try {
            CreateFacilityRequest request = new CreateFacilityRequest(name, type, capacity, description);
            FacilityDto createdFacility = facilityService.createFacility(request, pictures);
            return ResponseEntity.ok(createdFacility);
        } catch (Exception e) {
            log.error("Error creating facility: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable String id) {
        FacilityDto facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(facility);
    }
}
