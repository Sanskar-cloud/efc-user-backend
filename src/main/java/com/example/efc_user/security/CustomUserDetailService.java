package com.example.efc_user.security;



import com.example.efc_user.collections.DeliveryBoy;
import com.example.efc_user.collections.User;
import com.example.efc_user.exceptions.ResourceNotFoundException;
import com.example.efc_user.repo.DeliveryBoyRepo;
import com.example.efc_user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service

public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DeliveryBoyRepo deliveryBoyRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if it's a user
        Optional<User> user = this.userRepo.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }

        // Check if it's a delivery boy
        Optional<DeliveryBoy> deliveryBoy = this.deliveryBoyRepo.findByEmail(username);
        if (deliveryBoy.isPresent()) {
            return deliveryBoy.get();
        }

        // Throw exception if not found
        throw new UsernameNotFoundException("No user or delivery boy found with email: " + username);
    }
}

