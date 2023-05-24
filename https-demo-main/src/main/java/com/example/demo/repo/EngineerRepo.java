package com.example.demo.repo;

import com.example.demo.model.EngineerProfile;
import com.example.demo.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Scope("singleton")
public interface EngineerRepo extends JpaRepository<EngineerProfile,Long> {
    List<EngineerProfile> findAllByIsActive(boolean active);

}
