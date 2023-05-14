package com.example.demo.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.id.GUIDGenerator;

import java.util.Properties;
import java.util.UUID;

public class EmailService {

    public void SendPasswordlessLoginEmail(String email)
    {
        //provide recipient's email ID
        String to = email;
        //provide sender's email ID
        String from = "jakartafrom@example.com";
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
            message.setContent("<p>Click the following link to log in:</p><br><p><a href=\\\"https://localhost:8443/login?token=" + generateToken() + ">Log in</a></p>", "text/html");
            //send the email message
            Transport.send(message);
            System.out.println("Email Message Sent Successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
