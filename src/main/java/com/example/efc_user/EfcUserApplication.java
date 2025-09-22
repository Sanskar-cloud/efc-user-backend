package com.example.efc_user;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@SpringBootApplication
public class EfcUserApplication {


	@Autowired
	private PasswordEncoder passwordEncoder;


	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
//		String bootstrapServers = "pkc-921jm.us-east-2.aws.confluent.cloud:9092";
//		String topicName = "order-topic";
//
//		Properties properties = new Properties();
//		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//		properties.put("security.protocol", "SASL_SSL");
//		properties.put("sasl.mechanism", "PLAIN");
//		properties.put("sasl.jaas.config",
//				"org.apache.kafka.common.security.plain.PlainLoginModule required username='JX6KI3MZOYGQ2U7A' password='S4Y4vF7th3DvkgZeNPWoBK3ZtLHpPwXLjWRJT/YQI8Lrrmb0natlJjf1sZWfYMhI';");
//
//		KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
//
//		try {
//			for (int i = 0; i < 10; i++) {
//				String message = "Message " + i;
//				producer.send(new ProducerRecord<>(topicName, Integer.toString(i), message));
//				System.out.println("Sent: " + message);
//			}
//		} finally {
//			producer.close();
//		}

		SpringApplication.run(EfcUserApplication.class, args);
	}

}
