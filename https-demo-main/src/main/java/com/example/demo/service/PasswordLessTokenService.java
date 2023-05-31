package com.example.demo.service;

import com.example.demo.model.PasswordlessToken;
import com.example.demo.model.User;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.repo.UserRepo;
import org.assertj.core.error.future.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordLessTokenService {
    @Autowired
    private PasswordlessTokenRepo passwordlessTokenRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    public void CreateNewToken(String username) {
        User user = userRepo.findByEmail(username);
        if (user == null)
        {
            System.out.println("No user found with username " + username);
            throw new IllegalArgumentException("Bad email!");
        }
        else
        {
            if (passwordlessTokenRepo.existsByUsername(username))
            {
                PasswordlessToken passwordlessToken = passwordlessTokenRepo.findByUsername(username);
                if (passwordlessToken.IsStillValid())
                {
                    System.out.println("Token already exists");
                    throw new IllegalArgumentException("Token already exists!");
                }
                else
                {
                    passwordlessTokenRepo.delete(passwordlessToken);
                    System.out.println("Brisem stari token!");
                }
            }

            PasswordlessToken passwordlessToken = new PasswordlessToken(user.getUsername());
            passwordlessTokenRepo.save(passwordlessToken);
            emailService.SendPasswordlessLoginEmail(username, String.valueOf(passwordlessToken.getUuid()));
        }
    }

    public User loadUserByToken(String tokenUUID) throws UsernameNotFoundException {

        PasswordlessToken passwordlessToken = passwordlessTokenRepo.findByUuid(UUID.fromString(tokenUUID));

        if(passwordlessToken != null)
        {
            if(passwordlessToken.IsStillValid())
            {
                User user = userRepo.findByEmail(passwordlessToken.getUsername());
                if (user == null) {
                    System.out.println("No user found with username " + passwordlessToken.getUsername());
                    throw new UsernameNotFoundException(String.format("No user found with username '%s'.", passwordlessToken.getUsername()));
                } else {
                    passwordlessToken.setUsed(true);
                    return user;
                }
            }
            else
            {
                if(passwordlessToken.getCreationTime().isBefore(LocalDateTime.now().minusMinutes(3)))
                {
                    passwordlessTokenRepo.delete(passwordlessToken);
                }
                System.out.println("token is no longer valid!");
                throw new UsernameNotFoundException("token is no longer valid!");
            }
        }
        else
        {
            System.out.println("No token found with uuid: " + tokenUUID);
            throw new UsernameNotFoundException("No token found with uuid: " + tokenUUID);
        }
    }
}
