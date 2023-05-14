package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.service.PasswordLessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController()
@RequestMapping(value = "/auth")
public class UserController {
    @Autowired
    PasswordLessTokenService passwordLessTokenService;

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(HttpServletRequest request, LoginDTO loginData) {
        System.out.println("HTTPS request successfully passed!");
        return new ResponseEntity<>("HTTPS request successfully passed!", HttpStatus.OK);
    }

    @PostMapping(value = "/passwordlesslogin")
    public ResponseEntity<String> passwordlessLogin(HttpServletRequest request, String username) {
        System.out.println("PasswordlessLogin zapocet!");
        passwordLessTokenService.CreateNewToken(username);
        return new ResponseEntity<>("HTTPS request successfully passed!", HttpStatus.OK);
    }
}
