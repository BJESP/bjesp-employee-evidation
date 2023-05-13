package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController()
@RequestMapping(value = "/auth")
public class UserController {

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(HttpServletRequest request, LoginDTO loginData) {
        System.out.println("HTTPS request successfully passed!");
        return new ResponseEntity<>("HTTPS request successfully passed!", HttpStatus.OK);
    }

}
