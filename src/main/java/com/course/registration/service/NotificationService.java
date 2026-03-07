package com.course.registration.service;

import com.course.registration.config.JmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import jakarta.jms.Queue;

/**
 * JMS Producer — sends asynchronous enrollment notification messages to the
 * queue.
 */
@Service
public class NotificationService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Queue enrollmentQueue;

    /**
     * Sends an enrollment notification message to the JMS queue.
     * Format: studentEmail|studentName|courseName
     *
     * @param studentEmail Email of the enrolled student
     * @param studentName  Name of the enrolled student
     * @param courseName   Name of the course enrolled in
     */
    public void sendEnrollmentNotification(String studentEmail, String studentName, String courseName) {
        String message = studentEmail + "|" + studentName + "|" + courseName;
        jmsTemplate.convertAndSend(enrollmentQueue, message);
        System.out.println("[JMS PRODUCER] Notification sent to queue: " + message);
    }
}
