package com.example.demo.repo;

import com.example.demo.model.RegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationTokenRepo extends JpaRepository<RegistrationToken, Long> {
    RegistrationToken findByToken(String token);
    void removeByToken(String token);
}
