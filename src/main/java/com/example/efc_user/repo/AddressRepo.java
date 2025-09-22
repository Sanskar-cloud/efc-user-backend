package com.example.efc_user.repo;

import com.example.efc_user.collections.Address;
import com.example.efc_user.collections.Bookings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepo extends MongoRepository<Address, String> {

}
