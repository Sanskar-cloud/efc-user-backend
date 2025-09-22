package com.example.efc_user.config;




import com.example.efc_user.payloads.AdminUpdateRequest;
import com.example.efc_user.payloads.LocationUpdate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.JsonDeserializer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Log4j2
@AllArgsConstructor
public class KafkaConsumerConfig {

    private final String kafkaBootstrapServers = "localhost:9092"; // Replace with your actual Kafka bootstrap servers.

    // Consumer Factory for LocationUpdate
    @Bean
    public ConsumerFactory<String, LocationUpdate> locationUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LocationUpdateDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "live-location-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // Disable auto commit for better control
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start consuming from the beginning if no offset is found
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Kafka Listener Container Factory for LocationUpdate
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LocationUpdate> locationUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LocationUpdate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(locationUpdateConsumerFactory());

        // Configure a DefaultErrorHandler with a backoff strategy
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3)); // Retry 3 times with 1-second interval
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
    @Bean
    public ConsumerFactory<String, AdminUpdateRequest> hallUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HallUpdateDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "hall-update-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // Disable auto commit for better control
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start consuming from the beginning if no offset is found
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Kafka Listener Container Factory for LocationUpdate
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AdminUpdateRequest> hallUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AdminUpdateRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hallUpdateConsumerFactory());

        // Configure a DefaultErrorHandler with a backoff strategy
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3)); // Retry 3 times with 1-second interval
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    // Add other consumer configurations below as needed for additional topics or models
}

