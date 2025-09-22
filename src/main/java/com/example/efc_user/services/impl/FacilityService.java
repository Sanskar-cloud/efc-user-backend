package com.example.efc_user.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.efc_user.collections.Facility;

import com.example.efc_user.config.BackblazeConfig;
import com.example.efc_user.config.BucketProperties;
import com.example.efc_user.payloads.CreateFacilityRequest;
import com.example.efc_user.payloads.FacilityDto;
import com.example.efc_user.repo.FacilityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepo facilityRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

//    @Autowired
//    private AmazonS3 amazonS3;




    public FacilityDto createFacility(CreateFacilityRequest request, MultipartFile[] pictures) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        // Upload each image to Cloudinary and store its URL
        for (MultipartFile picture : pictures) {
            String imageUrl = cloudinaryService.uploadFile(picture);
            imageUrls.add(imageUrl);
        }

        // Create Facility entity
        Facility facility = new Facility();
        facility.setName(request.getName());
        facility.setType(request.getType());
        facility.setCapacity(request.getCapacity());
        facility.setDescription(request.getDescription());
        facility.setPictureUrls(imageUrls); // Store the list of picture URLs
        facility.setBooked(false); // Default: not booked

        Facility savedFacility = facilityRepository.save(facility);
        return mapToDto(savedFacility);
    }


//    private String uploadPictureToB2(String bucketName, MultipartFile picture) throws IOException {
//        String fileName = "facilities/" + UUID.randomUUID() + "-" + picture.getOriginalFilename();
//
//        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, picture.getInputStream(), null)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
//
//        return amazonS3.getUrl(bucketName, fileName).toString();
//    }

    public List<FacilityDto> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public FacilityDto getFacilityById(String id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return mapToDto(facility);
    }

    private FacilityDto mapToDto(Facility facility) {
        FacilityDto dto = new FacilityDto();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setType(facility.getType());
        dto.setCapacity(facility.getCapacity());
        dto.setBooked(facility.isBooked());
        dto.setDescription(facility.getDescription());
        dto.setPictureUrls(facility.getPictureUrls());
        dto.setOfflineStartTime(facility.getOfflineStartTime());
        dto.setOfflineEndTime(facility.getOfflineEndTime());
        return dto;
    }
}
