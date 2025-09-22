package com.example.efc_user.repo;

import com.example.efc_user.collections.Facility;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacilityRepo extends MongoRepository<Facility, String> {
}

