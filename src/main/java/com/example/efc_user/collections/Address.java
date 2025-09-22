package com.example.efc_user.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@Document(collection = "address")
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Field("address_line1")
    private String addressLine1;

    @Field("address_line2")
    private String addressLine2;
    private String  orderId;

    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Getters and Setters
//    public String getAddressLine1() {
//        return addressLine1;
//    }
//
//    public void setAddressLine1(String addressLine1) {
//        this.addressLine1 = addressLine1;
//    }
//
//    public String getAddressLine2() {
//        return addressLine2;
//    }
//
//    public void setAddressLine2(String addressLine2) {
//        this.addressLine2 = addressLine2;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
}

