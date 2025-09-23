package com.example.efc_user.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAliveScheduler {

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "0 */5 * * * ?")
    public void callKeepAlive() {
        try {
            String url = "https://score360-7.onrender.com/api/keep-alive";
            restTemplate.getForObject(url, String.class);

            System.out.println("Keep-alive API called successfully.");
        } catch (Exception e) {
            System.err.println("Keep-alive API call failed: " + e.getMessage());
        }
    }
}

