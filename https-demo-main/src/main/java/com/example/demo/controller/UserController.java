package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegistrationDTO;
import com.example.demo.model.User;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.service.PasswordLessTokenService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController()
@RequestMapping(value = "/auth")
public class UserController {
    @Autowired
    PasswordLessTokenService passwordLessTokenService;
    @Autowired
    private UserService userService;

    @PostMapping(consumes="application/json", value="/register")
    public ResponseEntity<HttpStatus> registerUser(@RequestBody RegistrationDTO data) {
        try {
            userService.registerUser(data);
        } catch (Exception ignored) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
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

    @GetMapping(value="/passwordlesslogin/{token}")
    public ResponseEntity<User> passwordlessLoginWithToken(@PathVariable String token) {
        if (token == null || token.equals(""))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = passwordLessTokenService.loadUserByToken(token);
        if (user==null)
        {
            System.out.println("Nema usera sa tim tokenom!");
            return null;
        }
        else
        {
            System.out.println("User " + user.getEmail() +" ulogovan preko passwordlessa!");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping(value="/register/{email}")
    public ResponseEntity<User> emailExists(@PathVariable String email) {
        if (email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        User user = userService.findByEmail(email);
        if (user==null)
            return null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
