package com.example.demo.repo;

import com.example.demo.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

}
