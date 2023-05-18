package com.example.demo.service;

import com.example.demo.dto.EmployeeDTO;
import com.example.demo.dto.RegistrationDTO;
import com.example.demo.model.*;
import com.example.demo.repo.BlockedUserRepo;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.utils.HMAC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        Collection<Role> roles = new ArrayList<Role>();
        if(userRegDTO.getRole().equals("ROLE_HR_MANAGER")){
            roles.add(roleRepository.findByName("ROLE_HR_MANAGER"));
            user.setRoles(roles);
        } else if (userRegDTO.getRole().equals("ROLE_SOFTWARE_ENGINEER")) {
            roles.add(roleRepository.findByName("ROLE_SOFTWARE_ENGINEER"));
            user.setRoles(roles);
        } else if (userRegDTO.getRole().equals("ROLE_PROJECT_MANAGER")) {
            roles.add(roleRepository.findByName("ROLE_PROJECT_MANAGER"));
            user.setRoles(roles);
        }
        userRepository.save(user);
    }
    public void registerAdmin(RegistrationDTO userRegDTO) {
        User user = new User(userRegDTO);
        Collection<Role> roles = new ArrayList<Role>();
        roles.add(roleRepository.findByName("ROLE_ADMIN"));
        user.setRoles(roles);
        user.setActive(true);
        userRepository.save(user);
    }
    public void editAdmin(RegistrationDTO userRegDTO) {
        User user = userRepository.findByEmail(userRegDTO.getEmail());
        user.setFirstName(userRegDTO.getFirstName());
        user.setLastName(userRegDTO.getLastName());
        user.setPhoneNumber(userRegDTO.getPhoneNumber());
        user.setPassword(userRegDTO.getPassword());
        user.setTitle(userRegDTO.getTitle());
        user.getAddress().setCountry(userRegDTO.getCountry());
        user.getAddress().setCity(userRegDTO.getCity());
        user.getAddress().setStreet(userRegDTO.getStreet());
        user.getAddress().setStreetNumber(userRegDTO.getStreetNumber());
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
        if(user.getRoles().stream().iterator().next().getName().equals("ROLE_SOFTWARE_ENGINEER"))
            user.setDateOfEmployment(LocalDate.now());
        userRepository.save(user);
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
    public List<EmployeeDTO> getAll(){
        ArrayList<User> all = (ArrayList<User>) userRepository.findAllByIsActive(true);
        List<EmployeeDTO> dtos = new ArrayList<>();
        for(User u:all){
            EmployeeDTO dto = new EmployeeDTO(u.getEmail(),u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getAddress(), u.getTitle(), u.getRoles().iterator().next().getName());
            dtos.add(dto);
        }
        return dtos;
    }
}
