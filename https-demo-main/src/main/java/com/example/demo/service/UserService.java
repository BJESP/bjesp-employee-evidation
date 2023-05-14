package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;

import java.util.List;

public class UserService {

    private final UserRepo userRepository;

    public UserService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUserEmail(String userEmail) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUserEmail().equals(userEmail)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
