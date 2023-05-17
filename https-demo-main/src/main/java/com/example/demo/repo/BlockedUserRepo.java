package com.example.demo.repo;

import com.example.demo.model.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedUserRepo extends JpaRepository<BlockedUser, Long> {
    BlockedUser findByEmail(String email);
}
