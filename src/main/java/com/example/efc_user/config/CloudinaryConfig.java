package com.example.efc_user.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dk8ewxw5s",
                "api_key", "752449998857293",
                "api_secret", "2V1nC_6qd9k8qbt9tCK4NbPjzR0"
        ));
    }
}
