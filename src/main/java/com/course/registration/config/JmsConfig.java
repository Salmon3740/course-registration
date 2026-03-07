package com.course.registration.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.jms.Queue;

@Configuration
public class JmsConfig {

    public static final String ENROLLMENT_QUEUE = "enrollment.notification.queue";

    @Bean
    public Queue enrollmentQueue() {
        return new ActiveMQQueue(ENROLLMENT_QUEUE);
    }
}
