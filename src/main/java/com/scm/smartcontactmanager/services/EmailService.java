package com.scm.smartcontactmanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendSimpleMail(String to, String subject, String body) {
        // Try block to check for exceptions
        try {
            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(to);
            mailMessage.setText(body);
            mailMessage.setSubject(subject);
            // Sending the mail
            javaMailSender.send(mailMessage);
            // return "Mail Sent Successfully...";
            return true;
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            e.printStackTrace();
            // return "Error while Sending Mail";
            return false;
        }
    }

}
