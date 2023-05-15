package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegistrationDTO;
import com.example.demo.exception.RefreshTokenException;
import com.example.demo.model.*;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.security.TokenUtils;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.PasswordLessTokenService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/auth",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    PasswordLessTokenService passwordLessTokenService;
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
     private TokenUtils tokenUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;


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
    public ResponseEntity login( @RequestBody LoginDTO loginData) {
        String userEmail = loginData.getEmail();
        System.out.println(loginData.getEmail()+loginData.getPassword());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = tokenUtils.generateToken(userEmail);



        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new LoginInResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                 userDetails.getEmail()));
    }

    @PostMapping(value = "/passwordlesslogin")
    public ResponseEntity<String> passwordlessLogin(HttpServletRequest request, String username) {
        System.out.println("PasswordlessLogin zapocet!");
        passwordLessTokenService.CreateNewToken(username);
        return new ResponseEntity<>("HTTPS request successfully passed!", HttpStatus.OK);
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

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> RefreshTokenFunction( @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenUtils.generateTokenFromEmail(user.getUsername());
                    return ResponseEntity.ok(new MessageResponseRefreshToken(token, requestRefreshToken));
                })
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
}
