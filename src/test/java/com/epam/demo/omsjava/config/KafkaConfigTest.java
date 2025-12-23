package com.epam.demo.omsjava.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");
    }

    @Test
    void producerFactory_NotNull() {
        ProducerFactory<String, Object> factory = kafkaConfig.producerFactory();
        
        assertNotNull(factory);
    }

    @Test
    void kafkaTemplate_NotNull() {
        KafkaTemplate<String, Object> template = kafkaConfig.kafkaTemplate();
        
        assertNotNull(template);
    }

    @Test
    void consumerFactory_NotNull() {
        ConsumerFactory<String, Object> factory = kafkaConfig.consumerFactory();
        
        assertNotNull(factory);
    }

    @Test
    void kafkaListenerContainerFactory_NotNull() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            kafkaConfig.kafkaListenerContainerFactory();
        
        assertNotNull(factory);
    }

    @Test
    void orderEventsTopic_Configuration() {
        NewTopic topic = kafkaConfig.orderEventsTopic();
        
        assertNotNull(topic);
        assertEquals("order-events", topic.name());
    }

    @Test
    void orderCompensationTopic_Configuration() {
        NewTopic topic = kafkaConfig.orderCompensationTopic();
        
        assertNotNull(topic);
        assertEquals("order-compensation", topic.name());
    }
}
