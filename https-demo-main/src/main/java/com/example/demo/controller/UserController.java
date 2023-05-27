package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exception.RefreshTokenException;
import com.example.demo.model.*;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.security.TokenUtils;
import com.example.demo.service.*;
import com.example.demo.utils.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
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
    @Autowired
    private RolePrivilegeService rolePrivilegeService;
    @PostMapping(value="/change-permission")
    @PreAuthorize("hasPermission(1, 'Permission', 'CREATE')")
    public ResponseEntity ChangeRolePermissions(@RequestBody RolePrivilegeDTO rolePrivilegeDTO){
            rolePrivilegeService.AddRolePermission(rolePrivilegeDTO);
            return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping(value="/get-not-role-permissions/{roleId}")
    @PreAuthorize("hasPermission(1, 'Permission', 'READ')")
    public ResponseEntity GetNotRolePermissions(@PathVariable Long roleId){
            List<Privilege> privileges = rolePrivilegeService.GetNotRolePermissions(roleId);
            return new ResponseEntity<>(privileges,HttpStatus.OK);
    }

    @GetMapping(value="/get-role-permissions/{roleId}")
    @PreAuthorize("hasPermission(1, 'Permission', 'READ')")
    public ResponseEntity GetRolePermissions(@PathVariable Long roleId){
            List<Privilege> privileges = rolePrivilegeService.GetRolePermissions(roleId);
            return new ResponseEntity<>(privileges,HttpStatus.OK);
    }


    @PostMapping(value="/delete-permission")
    @PreAuthorize("hasPermission(1, 'Permission', 'DELETE')")
    public ResponseEntity DeleteRolePermission(@RequestBody RolePrivilegeDTO rolePrivilegeDTO){
            rolePrivilegeService.DeleteRolePermission(rolePrivilegeDTO);
            return new ResponseEntity(HttpStatus.OK);

    }
    @GetMapping(value="/get-permission/{roleId}")
    public ResponseEntity GetPermissionForRole(@PathVariable Long roleId){
        Role role = rolePrivilegeService.GetRoleById(roleId);
        List<Privilege> newList  =(List) role.getPrivileges();
        return new ResponseEntity<>(newList,HttpStatus.OK);

    }

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
    public ResponseEntity passwordlessLogin(@RequestBody PasswordlessLoginDTO passwordlessLoginDTO) {
        System.out.println("PasswordlessLogin zapocet!");
        try {
            passwordLessTokenService.CreateNewToken(passwordlessLoginDTO.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            System.out.println("PUKSAO JE: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
    @PreAuthorize("hasPermission(1, 'User_status', 'UPDATE')")
    public ResponseEntity<HttpStatus> approveUser(@PathVariable String email) {
            if (email == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            userService.approveRegistrationRequest(email);
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/deny/{email}/{reason}")
    @PreAuthorize("hasPermission(1, 'User_status', 'UPDATE')")
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
    @GetMapping(value="/loggedInUser",produces=MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity getLoggedInUser(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = userService.findByEmail(email);
        UserDTO userDTO = new UserDTO(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getAddress(),user.getPhoneNumber());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
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
    @PreAuthorize("hasPermission(1, 'Employees', 'READ')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(HttpServletRequest request) {
            return new ResponseEntity<List<EmployeeDTO>>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping(consumes="application/json", value="/register/admin")
    @PreAuthorize("hasPermission(1, 'Admin_account', 'CREATE')")
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
    @PreAuthorize("hasPermission(1, 'Admin_account', 'UPDATE')")
    public ResponseEntity<HttpStatus> editProfileAdmin(@RequestBody RegistrationDTO data) {
            if (data.getPassword()!=null) {
                if (!passwordValidator.isValid(data.getPassword()))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            try {
                userService.editAdmin(data);
            } catch (Exception ignored) {
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
            }

            return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/register/request")
    @PreAuthorize("hasPermission(1, 'Registration_requests', 'READ')")
    public ResponseEntity<List<RegistrationRequestDTO>> getRegistrationRequests(HttpServletRequest request) {
            return new ResponseEntity<List<RegistrationRequestDTO>>(userService.getRegistrationRequests(), HttpStatus.OK);
    }
    @GetMapping(value="/loggedInAdmin")
    public ResponseEntity<RegistrationDTO> getLoggedInAdmin(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = userService.findByEmail(email);
        RegistrationDTO dto = new RegistrationDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCountry(user.getAddress().getCountry());
        dto.setCity(user.getAddress().getCity());
        dto.setStreet(user.getAddress().getStreet());
        dto.setStreetNumber(user.getAddress().getStreetNumber());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setTitle(user.getTitle());
        return new ResponseEntity<RegistrationDTO>(dto, HttpStatus.OK);
    }
    @GetMapping("/isInitial")
    public ResponseEntity<Boolean> getInitialAdmin(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        User user = userService.findByEmail(email);
        return new ResponseEntity<Boolean>(userService.isInitial(user), HttpStatus.OK);
    }


    @PostMapping("/edit/admin/pass/{pass}")
    @PreAuthorize("hasPermission(1, 'Initial_password', 'UPDATE')")
    public ResponseEntity<HttpStatus> changeInitialPassword(HttpServletRequest request, @PathVariable String pass) {
            if (!passwordValidator.isValid(pass))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            String email = tokenUtils.getEmailDirectlyFromHeader(request);
            if (email == null)
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            User user = userService.findByEmail(email);
            userService.changeInitialPassword(user, pass);
            return new ResponseEntity<>(HttpStatus.OK);
    }


    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }




}
