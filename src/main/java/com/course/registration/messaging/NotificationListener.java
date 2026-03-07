package com.course.registration.messaging;

import com.course.registration.config.JmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * JMS Consumer — listens to the enrollment notification queue and processes
 * messages asynchronously.
 * Simulates sending an email notification to the student.
 */
@Component
public class NotificationListener {

    @Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @JmsListener(destination = JmsConfig.ENROLLMENT_QUEUE)
    public void receiveEnrollmentNotification(String message) {
        System.out.println("========================================================");
        System.out.println("[EMAIL NOTIFICATION] Processing message from queue...");

        try {
            // Message format: studentEmail|studentName|courseName
            String[] parts = message.split("\\|");
            if (parts.length < 3) {
                System.err.println("[EMAIL ERROR] Invalid message format: " + message);
                return;
            }

            String studentEmail = parts[0];
            String studentName = parts[1];
            String courseName = parts[2];

            System.out.println("[EMAIL NOTIFICATION] Sending email to: " + studentEmail);

            org.springframework.mail.SimpleMailMessage mailMessage = new org.springframework.mail.SimpleMailMessage();
            mailMessage.setTo(studentEmail);
            mailMessage.setSubject("Enrollment Confirmation: " + courseName);
            mailMessage.setText("Dear " + studentName + ",\n\n" +
                    "You have successfully enrolled in the course: " + courseName + ".\n\n" +
                    "Best regards,\n" +
                    "Registration System Team");

            mailSender.send(mailMessage);
            System.out.println("[EMAIL NOTIFICATION] Email sent successfully to " + studentEmail);
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Failed to send email: " + e.getMessage());
            // We don't throw exception here to avoid infinite JMS retries if SMTP is down
        }
        System.out.println("========================================================");
    }
}
