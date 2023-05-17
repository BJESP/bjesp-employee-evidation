package com.example.demo.service;

import com.example.demo.dto.RegistrationDTO;
import com.example.demo.model.BlockedUser;
import com.example.demo.model.RegistrationToken;
import com.example.demo.model.User;
import com.example.demo.repo.BlockedUserRepo;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.utils.HMAC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final  EmailService emailService;
    private final  RegistrationTokenService registrationTokenService;
    private final HMAC HMACService;
    private final BlockedUserRepo blockedUserRepository;

    public UserService(UserRepo userRepository, RoleRepo roleRepository, EmailService emailService, RegistrationTokenService registrationTokenService, HMAC hmacService, BlockedUserRepo blockedUserRepository) {
        this.userRepository = userRepository;

        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.registrationTokenService = registrationTokenService;
        HMACService = hmacService;
        this.blockedUserRepository = blockedUserRepository;
    }

    public User findByUserEmail(String userEmail) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getEmail().equals(userEmail)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public void registerUser(RegistrationDTO userRegDTO) {
        User user = new User(userRegDTO);
        if(userRegDTO.getRole().equals("ROLE_HR_MANAGER")){
            user.setRole(roleRepository.findByName("ROLE_HR_MANAGER"));
        } else if (userRegDTO.getRole().equals("ROLE_SOFTWARE_ENGINEER")) {
            user.setRole(roleRepository.findByName("ROLE_SOFTWARE_ENGINEER"));
        } else if (userRegDTO.getRole().equals("ROLE_PROJECT_MANAGER")) {
            user.setRole(roleRepository.findByName("ROLE_PROJECT_MANAGER"));
        }
        userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void approveRegistrationRequest(String email) {
        User user = userRepository.findByEmail(email);
        emailService.sendRegistrationEmail(user);
    }

    public void denyRegistrationRequest(String email, String reason) {
        User user = userRepository.findByEmail(email);
        emailService.sendRegistrationDeniedEmail(email, reason);
        BlockedUser bu = new BlockedUser();
        bu.setEmail(user.getEmail());
        bu.setBlockedUntil(LocalDateTime.now().plusMonths(1));
        blockedUserRepository.save(bu);
        userRepository.delete(user);
    }
    public boolean verifyUser(String token, String hmac) throws Exception{
        RegistrationToken secureToken = registrationTokenService.findByToken(token);
        User user = userRepository.getOne(secureToken.getUser().getId());
        if (Objects.isNull(user) || Objects.isNull(secureToken)) {
            return false;
        }
        if(secureToken.isExpired()) {
            registrationTokenService.removeToken(secureToken);
            userRepository.delete(user);
        }
        if (Objects.isNull(secureToken) || !token.equals(secureToken.getToken()) || secureToken.isExpired()) {
            throw new Exception("Invalid token");
        }

        // Verify the HMAC
        boolean hmacValid = HMACService.verifyHmac(user.getEmail(), hmac.replace(" ", "+"), "my_secret_key");
        if (!hmacValid) {
            return false;
        }
        user.setActive(true);
        userRepository.save(user);
        //ovde dodati ono za inz za pocetak rada!!
        //brise se da bi moglo samo jednom da se iskoristi
        registrationTokenService.removeToken(secureToken);
        return true;
    }
    public boolean isBlocked(String email){
        BlockedUser bu = blockedUserRepository.findByEmail(email);
        if(bu == null)
            return false;
        return bu.isBlocked();
    }
}
