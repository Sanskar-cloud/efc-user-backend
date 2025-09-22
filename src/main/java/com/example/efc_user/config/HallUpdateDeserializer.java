package com.example.efc_user.config;

import com.example.efc_user.payloads.AdminUpdateRequest;
import com.example.efc_user.payloads.LocationUpdate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Deserializer;

@Log4j2
public class HallUpdateDeserializer implements Deserializer<AdminUpdateRequest> {

    @Override
    public AdminUpdateRequest deserialize(String s, byte[] bytes) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown properties
        AdminUpdateRequest tableStatusModel = null;
        try {
            tableStatusModel = objectMapper.readValue(bytes, AdminUpdateRequest.class);
        } catch (Exception e) {
            log.info("Error in deserializing JobStatusModel::{}", e.getMessage());
        }
        return tableStatusModel;
    }
}
