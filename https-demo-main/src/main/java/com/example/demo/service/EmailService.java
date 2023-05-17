package com.example.demo.service;

import com.example.demo.model.RegistrationToken;
import com.example.demo.model.User;
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

    public void SendPasswordlessLoginEmail(String email, String tokenUUID)
    {
        //provide recipient's email ID
        String to = email;
        //provide sender's email ID
        String from = "bjesp@example.com";
        //provide Mailtrap's username
        final String username = "06049838345593";
        //provide Mailtrap's password
        final String password = "6c10df52240153";
        //provide Mailtrap's host address
        String host = "sandbox.smtp.mailtrap.io";
        //configure Mailtrap's SMTP server details
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
        //create the Session object
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(props, authenticator);
        try {
            //create a MimeMessage object
            Message message = new MimeMessage(session);
            //set From email field
            message.setFrom(new InternetAddress(from));
            //set To email field
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            //set email subject field
            message.setSubject("Here comes Jakarta Mail!");
            //set the content of the email message
            message.setContent("<p>Click the following link to log in:</p><br><p><a href=\"" + tokenUUID + "\">Log in</a></p>", "text/html");
            //send the email message
            Transport.send(message);
            System.out.println("Email Message Sent Successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
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

}
