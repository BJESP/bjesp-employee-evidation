package com.example.demo.service;

import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.RegistrationToken;
import com.example.demo.model.User;
import com.example.demo.repo.PasswordResetTokenRepo;
import com.example.demo.repo.RegistrationTokenRepo;
import com.example.demo.utils.HMAC;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.id.GUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RegistrationTokenService registrationTokenService;
    @Autowired
    private RegistrationTokenRepo registrationTokenRepository;
    @Autowired
    private HMAC HMACService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepository;

    public void SendPasswordlessLoginEmail(String email, String tokenUUID)
    {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("isa.hospitall@gmail.com");
        message.setTo(email);
        String verificationLink = "https://localhost:4200/auth/passwordless-login-t?token=" +tokenUUID;
        String body = "Click here to login to your account:" + verificationLink ;
        message.setText(body);
        message.setSubject("Confirm login");

        mailSender.send(message);

        System.out.println("mail sent successfully");
    }
    @Async
    public void sendRegistrationEmail(User user){
        RegistrationToken secureToken= registrationTokenService.createSecureToken();
        secureToken.setUser(user);
        registrationTokenRepository.save(secureToken);
        String hmac = HMACService.generateHmac(user.getEmail(), "my_secret_key");
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("isa.hospitall@gmail.com");
        message.setTo(user.getEmail());
        String verificationLink = "https://localhost:8000/auth/confirm-mail?token=" + secureToken.getToken() + "&hmac=" + hmac;
        String body = "Click here to activate your account:" + verificationLink ;
        message.setText(body);
        message.setSubject("Confirm registration");

        mailSender.send(message);

        System.out.println("mail sent successfully");
    }
    @Async
    public void sendRegistrationDeniedEmail(String toEmail,String reason){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("isa.hospitall@gmail.com");
        message.setTo(toEmail);
        message.setText("Rejection reason: " + reason);
        message.setSubject("Registration denied");

        mailSender.send(message);

        System.out.println("mail sent successfully");
    }
    @Async
    public void sendResetEmail(User user){
        PasswordResetToken secureToken= passwordResetTokenService.createSecureToken();
        secureToken.setUser(user);
        passwordResetTokenRepository.save(secureToken);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("isa.hospitall@gmail.com");
        message.setTo(user.getEmail());
        String verificationLink = "https://localhost:4200/new-password/confirm-mail?token=" + secureToken.getToken();
        String body = "Click here to reset your password:" + verificationLink ;
        message.setText(body);
        message.setSubject("Reset password");

        mailSender.send(message);

        System.out.println("mail sent successfully");
    }

    @Async
    public void sendWarningEmail(String toEmail,String messageError){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("isa.hospitall@gmail.com");
        message.setTo(toEmail);
        message.setText(messageError );
        message.setSubject("Suspicious action-warning");

        mailSender.send(message);

        System.out.println("mail sent successfully");
    }

}
