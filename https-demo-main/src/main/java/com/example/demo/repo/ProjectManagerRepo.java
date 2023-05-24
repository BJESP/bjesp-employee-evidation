package com.example.demo.repo;

import com.example.demo.model.EngineerProfile;
import com.example.demo.model.ProjectManagerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectManagerRepo extends JpaRepository<ProjectManagerProfile,Long> {
    List<ProjectManagerProfile> findAllByIsActive(boolean active);

    ProjectManagerProfile findByEmail(String email);
}
