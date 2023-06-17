package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exception.RefreshTokenException;
import com.example.demo.model.*;
import com.example.demo.repo.PasswordlessTokenRepo;
import com.example.demo.security.TokenUtils;
import com.example.demo.service.*;
import com.example.demo.utils.PasswordValidator;
import com.example.demo.utils.UserValidation;
import org.apache.logging.log4j.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    @Autowired
    private UserValidation userValidation;

    private static  Logger logger =  LoggerFactory.getLogger(UserController.class);

    @PostMapping(value="/change-permission")
    @PreAuthorize("hasPermission(1, 'Permission', 'CREATE')")
    public ResponseEntity ChangeRolePermissions(@RequestBody RolePrivilegeDTO rolePrivilegeDTO){
            logger.info("Changed role permission");
            rolePrivilegeService.AddRolePermission(rolePrivilegeDTO);

            return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping(value="/get-not-role-permissions/{roleId}")
    @PreAuthorize("hasPermission(1, 'Permission', 'READ')")
    public ResponseEntity GetNotRolePermissions(@PathVariable Long roleId){
        try {
            List<Privilege> privileges = rolePrivilegeService.GetNotRolePermissions(roleId);

            return new ResponseEntity<>(privileges, HttpStatus.OK);
        }
        catch(NullPointerException e){
            return new ResponseEntity(HttpStatus.OK);
        }
    }



    @PostMapping(value="/get-role-permissions")
    public ResponseEntity<List<Privilege>> GetRolePermissions(@RequestBody RolesDTO rolesDTO){


            List<Privilege> privileges = rolePrivilegeService.GetRolePermissions(rolesDTO);

            return new ResponseEntity<List<Privilege>>(privileges,HttpStatus.OK);
    }

    @PostMapping(value="/check-permission")
   // @PreAuthorize("hasPermission()")
    public ResponseEntity CheckPermission(@RequestBody RolePrivilegeDTO rolePrivilegeDTO){
            boolean hasPermission = rolePrivilegeService.CheckPermission(rolePrivilegeDTO);
            if(hasPermission) {
                logger.info("User has permission");
                return new ResponseEntity(hasPermission, HttpStatus.OK);
            }
            else {
                logger.error("User doesn't have permission");
                return new ResponseEntity( HttpStatus.BAD_REQUEST);
            }



    }


    @PostMapping(value="/delete-permission")
    @PreAuthorize("hasPermission(1, 'Permission', 'DELETE')")
    public ResponseEntity DeleteRolePermission(@RequestBody RolePrivilegeDTO rolePrivilegeDTO){

            rolePrivilegeService.DeleteRolePermission(rolePrivilegeDTO);
             logger.info("Successfully deleted role permission");
            return new ResponseEntity(HttpStatus.OK);

    }
    @GetMapping(value="/get-permission/{roleId}")
    public ResponseEntity GetPermissionForRole(@PathVariable Long roleId){
        Role role = rolePrivilegeService.GetRoleById(roleId);
        List<Privilege> newList  =(List) role.getPrivileges();
        return new ResponseEntity<>(newList,HttpStatus.OK);

    }

    @PostMapping(consumes="application/json", value="/register")
    public ResponseEntity registerUser(@RequestBody RegistrationDTO data) {
        if(userService.isBlocked(data.getEmail())) {
            logger.error("User is blocked");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!passwordValidator.isValid(data.getPassword())) {
            logger.error("Password doesnt meet requirements");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        try {
//            userService.registerUser(data);
//        } catch (Exception ignored) {
//            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
//        }
//
//        return new ResponseEntity<>(HttpStatus.OK);
        try {
            ValidationResult validationResult = userValidation.validRegistrationDTO(data);
            if (validationResult.isValid()) {
                userService.registerUser(data);
                logger.info("User registered");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.error("User information is not valid");
                return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }
    @PostMapping(value = "/login")
    public ResponseEntity login(HttpServletRequest request, @RequestBody LoginDTO loginData) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        ServletServerHttpRequest servletRequest = new ServletServerHttpRequest(requestAttributes.getRequest());
        String ipAddress = servletRequest.getRemoteAddress().getAddress().getHostAddress();
        String userEmail = loginData.getEmail();
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roleNames = GetRoleNames(userDetails);
            String jwt = tokenUtils.generateToken(userEmail, roleNames);


            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());
            logger.info("User successfully logged in from"+"IP:" +ipAddress+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
            return ResponseEntity.ok(new LoginInResponse(jwt, refreshToken.getToken(), userDetails.getUser().getId(),
                    userDetails.getUsername(), roleNames));
        }
        catch(Exception e){
            logger.error("Bad credentials");
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(consumes="application/json",value = "/passwordlesslogin")
    public ResponseEntity passwordlessLogin(@RequestBody PasswordlessLoginDTO passwordlessLoginDTO) {
        System.out.println("PasswordlessLogin zapocet!");
        try {

            passwordLessTokenService.CreateNewToken(passwordlessLoginDTO.getUsername());
            logger.info("Token successfully created ");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            logger.error(e.getMessage()+"during passwordless login");
            System.out.println("PUKSAO JE: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value="/passwordless-login", consumes = "*/*")
    public ResponseEntity passwordlessLoginToken(@RequestParam("token") String token)
    {
        if (token == null || token.equals("")) {
            logger.info("Token doesn't exist");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = passwordLessTokenService.loadUserByToken(token);
        if (user==null)
        {
            logger.info("User failed to log in");
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
        logger.info("User successfully logged in  from");
        return ResponseEntity.ok(new LoginInResponse(jwt, refreshToken.getToken(), 0L,
                user.getEmail(),roleNames));

    }


    @GetMapping(value="/register/{email}")

    public ResponseEntity<UserDTO> emailExists(@PathVariable String email) {
        System.out.println(email);
        if (email == null) {
            logger.error("email not found");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userService.findByEmail(email);
        UserDTO dto = new UserDTO();
        if (user == null) {
            logger.error("User not found");
            return null;
        }
        dto.setEmail(user.getEmail());
        logger.info("User found" + user.getId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }




    @PostMapping("/refreshtoken")
    public ResponseEntity<?> RefreshTokenFunction( @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        logger.info("New access token successfully created with refresh token");
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
            if (email == null) {
                logger.error("email not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            userService.approveRegistrationRequest(email);
            logger.info("User request for registration approved");
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/deny/{email}/{reason}")
    @PreAuthorize("hasPermission(1, 'User_status', 'UPDATE')")
    public ResponseEntity<HttpStatus> denyUser(@PathVariable String email,@PathVariable String reason) {
            if (email == null) {
                logger.error("email not found");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            userService.denyRegistrationRequest(email, reason);
            logger.info("User request for registration denied");
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value="/confirm-mail", consumes = "*/*")
    public ResponseEntity<HttpStatus> activateUserAccount(@RequestParam("token") String token, @RequestParam("hmac") String hmac){
        try {
            userService.verifyUser(token, hmac);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("User account activated successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value="/loggedInUser",produces=MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity getLoggedInUser(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null) {
            logger.error("email not found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.findByEmail(email);
        UserDTO userDTO = new UserDTO(user.getId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getAddress(),user.getPhoneNumber());
        logger.info("Logged in user"+user.getId());
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
        logger.info("Reading all employees");
            return new ResponseEntity<List<EmployeeDTO>>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping(consumes="application/json", value="/register/admin")
    @PreAuthorize("hasPermission(1, 'Admin_account', 'CREATE')")
    public ResponseEntity registerAdmin(@RequestBody RegistrationDTO data,HttpServletRequest request) {
            if(userService.isBlocked(data.getEmail())) {
                logger.error("User is blocked, cant access account");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!passwordValidator.isValid(data.getPassword())) {
                logger.warn("Invalid password, didn't meet all requirements"+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
//            try {
//                userService.registerAdmin(data);
//            } catch (Exception ignored) {
//                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
//            }
//
//            return new ResponseEntity<>(HttpStatus.OK);
            try {
                ValidationResult validationResult = userValidation.validRegistrationDTO(data);
                if (validationResult.isValid()) {
                    logger.info("Successfully register admin "+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
                    userService.registerAdmin(data);
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    logger.error("Unsuccessfully register admin"+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
                    return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            }catch (IllegalArgumentException e) {
                logger.warn("Illegal argument"+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }
    @PostMapping(consumes="application/json", value="/edit/admin")
    @PreAuthorize("hasPermission(1, 'Admin_account', 'UPDATE')")
    public ResponseEntity editProfileAdmin(@RequestBody RegistrationDTO data,HttpServletRequest request) {
            if (data.getPassword()!=null) {
                if (!passwordValidator.isValid(data.getPassword())) {
                    logger.warn("Invalid password"+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
//            try {
//                userService.editAdmin(data);
//            } catch (Exception ignored) {
//                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
//            }
//
//            return new ResponseEntity<>(HttpStatus.OK);
        try {
            ValidationResult validationResult = userValidation.validRegistrationDTO(data);
            if (validationResult.isValid()) {

                userService.editAdmin(data);
                logger.info("Successfully edit admin information");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.error(validationResult.getErrorMessage());
                return new ResponseEntity<>(validationResult.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
        }catch (IllegalArgumentException e) {
            logger.error(e.getMessage()+"during edit of admin profile");
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }
    @GetMapping("/register/request")
    @PreAuthorize("hasPermission(1, 'Registration_requests', 'READ')")
    public ResponseEntity<List<RegistrationRequestDTO>> getRegistrationRequests(HttpServletRequest request) {
            return new ResponseEntity<List<RegistrationRequestDTO>>(userService.getRegistrationRequests(), HttpStatus.OK);
    }
    @GetMapping(value="/loggedInAdmin")
    public ResponseEntity<RegistrationDTO> getLoggedInAdmin(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null) {
            logger.error("Admin not found ");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
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
        logger.info("Admin found"+user.getId());
        return new ResponseEntity<RegistrationDTO>(dto, HttpStatus.OK);
    }
    @GetMapping("/isInitial")
    public ResponseEntity<Boolean> getInitialAdmin(HttpServletRequest request) {
        String email = tokenUtils.getEmailDirectlyFromHeader(request);
        if (email == null) {
            logger.error("Admin not found ");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.findByEmail(email);
        logger.info("Admin found and checked if is initial"+user.getId());
        return new ResponseEntity<Boolean>(userService.isInitial(user), HttpStatus.OK);
    }


    @PostMapping("/edit/admin/pass/{pass}")
    @PreAuthorize("hasPermission(1, 'Initial_password', 'UPDATE')")
    public ResponseEntity<HttpStatus> changeInitialPassword(HttpServletRequest request, @PathVariable String pass) {
            if (!passwordValidator.isValid(pass)) {
                logger.warn("Invalid password");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            String email = tokenUtils.getEmailDirectlyFromHeader(request);
            if (email == null) {
                logger.error("User not found");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            User user = userService.findByEmail(email);
            userService.changeInitialPassword(user, pass);
            logger.info("User initial password changed");
            return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/search")
    @PreAuthorize("hasPermission(1, 'Employees', 'READ')")
    public ResponseEntity<List<EngineerSearchDTO>> searchEngineers(@RequestBody EngineerSearchDTO data) {
        return new ResponseEntity<>(userService.searchEngineers(data), HttpStatus.OK);
    }
    @GetMapping("/engineers/search")
    @PreAuthorize("hasPermission(1, 'Employees', 'READ')")
    public ResponseEntity<List<EngineerSearchDTO>> getEngineersForSearch(HttpServletRequest request) {
        return new ResponseEntity<>(userService.getEngineersForSearch(), HttpStatus.OK);
    }


    public boolean CheckPermissionForRole(String privilege){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasPermission = userService.CheckPermissionForRole(authentication,privilege);
        return hasPermission;

    }
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    @PostMapping("/password-request/{email}")
    public ResponseEntity<HttpStatus> resetPasswordRequest(@PathVariable String email) {
        if (email == null) {
            logger.error("email not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.resetPasswordRequest(email);
        logger.info("Password reset request sent");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/new-password")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestBody ResetPasswordDTO data) {
        if (data.getPassword() == null) {
            logger.error("password not found");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!passwordValidator.isValid(data.getPassword())) {
            logger.warn("Invalid password, didn't meet all requirements"+"IP:" +getClientIpAddress(request)+" HOST:"+request.getRemoteHost()+ "PORT:"+request.getRemotePort());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            userService.resetPassword(data);
        } catch (Exception e) {
            logger.error(e.getMessage()+"during password reset");
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        logger.info("Password reset");
        return new ResponseEntity<>(HttpStatus.OK);
    }








}
