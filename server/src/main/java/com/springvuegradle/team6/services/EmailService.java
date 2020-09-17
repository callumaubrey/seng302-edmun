package com.springvuegradle.team6.services;

import com.springvuegradle.team6.models.entities.Email;
import com.springvuegradle.team6.security.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service("EmailService")
public class EmailService {

    @Autowired
    private JavaMailSenderImpl emailSender;

    @Autowired
    ConfigProperties configProperties;

    public void sendPasswordTokenEmail(String to, String subject, String token, String userName) {
        emailSender.setUsername(configProperties.getUsername());
        emailSender.setPassword(configProperties.getPassword());
        try {
            MimeMessage message = emailSender.createMimeMessage();
            SimpleMailMessage message1 = new SimpleMailMessage();
            message.setFrom("no_reply@edmun.com");
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("no_reply@edmun.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("<html><body>Hi, " + userName + ". <br><br>Click the link below to reset your password. <br><br>http://localhost:9500/resetpassword/" + token + "</body></html>", true);


            emailSender.send(message);
        } catch (MailException | MessagingException exception) {
            exception.printStackTrace();
        }
    }
}
