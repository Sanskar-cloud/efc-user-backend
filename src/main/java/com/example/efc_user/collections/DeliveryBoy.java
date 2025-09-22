package com.example.efc_user.collections;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "delivery_boy")
public class DeliveryBoy implements UserDetails {

    @Id
    private String id; // Delivery Boy ID

    private String name; // Login username
    private String password; // Login password (preferably hashed)
    private String phone;
    private String email;
    private LocalDateTime dateOfJoining;
    private double averageRating;
    private int totalReviews;

    public void updateRating(double newRating) {
        totalReviews++;
        averageRating = (averageRating * (totalReviews - 1) + newRating) / totalReviews;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

