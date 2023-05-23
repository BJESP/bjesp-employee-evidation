package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exception.RefreshTokenException;
import com.example.demo.model.*;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.security.TokenUtils;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.PasswordLessTokenService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.service.UserService;
import com.example.demo.utils.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/auth",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    PasswordLessTokenService passwordLessTokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordValidator passwordValidator;

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
        if(userService.isBlocked(data.getEmail()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!passwordValidator.isValid(data.getPassword()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        List<String> roleNames = GetRoleNames(userDetails);
        String jwt = tokenUtils.generateToken(userEmail, roleNames);



        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        return ResponseEntity.ok(new LoginInResponse(jwt, refreshToken.getToken(), userDetails.getUser().getId(),
                 userDetails.getUsername(),roleNames));
    }

    @PostMapping(consumes="application/json",value = "/passwordlesslogin")
    public ResponseEntity<String> passwordlessLogin(@RequestBody PasswordlessLoginDTO passwordlessLoginDTO) {
        System.out.println("PasswordlessLogin zapocet!");
        passwordLessTokenService.CreateNewToken(passwordlessLoginDTO.getUsername());
        return new ResponseEntity<>("HTTPS request successfully passed!", HttpStatus.OK);
    }

    @GetMapping(value="/passwordless-login", consumes = "*/*")
    public ResponseEntity passwordlessLoginToken(@RequestParam("token") String token)
    {
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

        Authentication authentication = (new UsernamePasswordAuthenticationToken(user.getEmail(), null));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<String> roleNames = GetRoleNames2(user);
        String jwt = tokenUtils.generateToken(user.getEmail(),roleNames);

        RefreshToken refreshToken = refreshTokenService.createRefreshTokenPasswordless(user);

        return ResponseEntity.ok(new LoginInResponse(jwt, refreshToken.getToken(), 0L,
                user.getEmail(),roleNames));

    }


    @GetMapping(value="/register/{email}")
    public ResponseEntity<User> emailExists(@PathVariable String email) {
        if (email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    @PostMapping("/approve/{email}")
    public ResponseEntity<HttpStatus> approveUser(@PathVariable String email) {
        if (email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        userService.approveRegistrationRequest(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/deny/{email}/{reason}")
    public ResponseEntity<HttpStatus> denyUser(@PathVariable String email,@PathVariable String reason) {
        if (email == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        userService.denyRegistrationRequest(email, reason);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value="/confirm-mail", consumes = "*/*")
    public ResponseEntity<HttpStatus> activateUserAccount(@RequestParam("token") String token, @RequestParam("hmac") String hmac){
        try {
            userService.verifyUser(token, hmac);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    List<String> GetRoleNames(UserDetailsImpl user){
        List<String> roleNames = new ArrayList<>();
        for(Role role:user.getUser().getRoles()){
            roleNames.add(role.getName());
        }
        return roleNames;

    }

    List<String> GetRoleNames2(User user) {
        List<String> roleNames = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    @GetMapping("/all")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(HttpServletRequest request) {
        return new ResponseEntity<List<EmployeeDTO>>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping(consumes="application/json", value="/register/admin")
    public ResponseEntity<HttpStatus> registerAdmin(@RequestBody RegistrationDTO data) {
        if(userService.isBlocked(data.getEmail()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!passwordValidator.isValid(data.getPassword()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            userService.registerAdmin(data);
        } catch (Exception ignored) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping(consumes="application/json", value="/edit/admin")
    public ResponseEntity<HttpStatus> editProfileAdmin(@RequestBody RegistrationDTO data) {
        if (!passwordValidator.isValid(data.getPassword()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            userService.editAdmin(data);
        } catch (Exception ignored) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/register/request")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationRequestDTO>> getRegistrationRequests(HttpServletRequest request) {
        return new ResponseEntity<List<RegistrationRequestDTO>>(userService.getRegistrationRequests(), HttpStatus.OK);
    }
}
