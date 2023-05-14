package com.example.demo.repo;

import com.example.demo.model.PasswordlessToken;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordlessTokenRepo extends JpaRepository<PasswordlessToken, Long>
{
    PasswordlessToken findByUuid(UUID uuid);
}
