package com.example.demo.service;

import com.example.demo.dto.RegistrationDTO;
import com.example.demo.model.User;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;

    public UserService(UserRepo userRepository, RoleRepo roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
}
