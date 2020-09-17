package com.springvuegradle.team6.services;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.security.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("EmailService")
public class EmailService {

    @Autowired
    private JavaMailSenderImpl emailSender;

    @Autowired
    ConfigProperties configProperties;

    public void sendSimpleMessage(String to, String subject, String text) {
        emailSender.setUsername(configProperties.getUsername());
        emailSender.setPassword(configProperties.getPassword());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no_reply@edmun.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }
}
