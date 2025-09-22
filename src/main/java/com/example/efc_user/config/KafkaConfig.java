package com.example.efc_user.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
@Log4j2
public class KafkaConfig {

    private final String bootstrapServers = "localhost:9092";
    private final String apiKey = "JX6KI3MZOYGQ2U7A";
    private final String apiSecret = "S4Y4vF7th3DvkgZeNPWoBK3ZtLHpPwXLjWRJT/YQI8Lrrmb0natlJjf1sZWfYMhI";

    // Kafka Admin Client Configuration
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

//         SASL/SSL Authentication for Confluent Cloud
//        configs.put("security.protocol", "SASL_SSL");
//        configs.put("sasl.mechanism", "PLAIN");
//        configs.put("sasl.jaas.config",
//                "org.apache.kafka.common.security.plain.PlainLoginModule required username='JX6KI3MZOYGQ2U7A' password='S4Y4vF7th3DvkgZeNPWoBK3ZtLHpPwXLjWRJT/YQI8Lrrmb0natlJjf1sZWfYMhI';");

        return new KafkaAdmin(configs);
    }

    // Creating Kafka Topic - order-topic
    @Bean
    public NewTopic orderTopic() {
        NewTopic topic = TopicBuilder.name("order-topic")
                .partitions(3)  // You can specify the number of partitions as per your need
                 // Specify the replication factor as per your use case
                .build();
        log.info("Created Kafka order topic: {}", topic);
        return topic;
    }
    @Bean
    public NewTopic deliveryLocationTopic() {
        // Sharded Kafka topic with 5 partitions for parallel processing
        return TopicBuilder.name("delivery_location_update")
                .partitions(3) // Number of partitions

                .build();
    }
    @Bean
    public NewTopic onlineBookingUpdateTopic() {
        // Sharded Kafka topic with 5 partitions for parallel processing
        return TopicBuilder.name("online_booking_update")
                .partitions(3) // Number of partitions

                .build();
    }
//    @Bean
//    public NewTopic adminUpdateTopic() {
//        // Sharded Kafka topic with 5 partitions for parallel processing
//        return TopicBuilder.name("admin_updates")
//                .partitions(3) // Number of partitions
//
//                .build();
//    }

}
