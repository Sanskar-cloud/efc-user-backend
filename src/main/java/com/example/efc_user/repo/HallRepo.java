package com.example.efc_user.repo;

import com.example.efc_user.collections.Hall;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HallRepo extends MongoRepository<Hall, String> {}
