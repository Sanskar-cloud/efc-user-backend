package com.example.efc_user.collections;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data

@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id; // Unique user ID

    private String name;
    private String email;
    private String password; // Hashed password
    private String phone;
    private String logoPath=null;

    private Address defaultAddress=null; // Default address of the user

    private List<String> orderIds; // References to order IDs

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


// Getters and Setters



