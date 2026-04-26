package com.course.registration.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    @Async
    public void sendRegistrationEmail(String toEmail, String userName, String courseName) {
        if (sendGridApiKey == null || sendGridApiKey.trim().isEmpty()) {
            System.err.println("[EMAIL ERROR] SendGrid API Key is not configured.");
            return;
        }
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            System.err.println("[EMAIL ERROR] 'From' email address is not configured.");
            return;
        }

        try {
            Email from = new Email(fromEmail.trim(), fromName);
            String subject = "Course Registration Successful";
            Email to = new Email(toEmail.trim());
            Content content = new Content("text/plain", 
                String.format("Dear %s,\n\nYou have successfully enrolled in the course: %s.\n\nConfirmation message: Your seat is reserved. Happy learning!\n\nBest regards,\nRegistration System Team", 
                userName, courseName));
            
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey.trim());
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("[EMAIL SUCCESS] Email sent successfully to: " + toEmail);
            } else {
                System.err.println("[EMAIL ERROR] SendGrid failure. Status: " + response.getStatusCode());
                System.err.println("[EMAIL ERROR] Response Body: " + response.getBody());
                System.err.println("[EMAIL ERROR] Hint: Ensure '" + fromEmail + "' is a Verified Sender in SendGrid.");
            }
        } catch (Exception ex) {
            System.err.println("[EMAIL ERROR] Exception while sending email: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
