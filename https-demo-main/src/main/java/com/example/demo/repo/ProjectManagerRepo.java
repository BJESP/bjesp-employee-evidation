package com.example.demo.repo;

import com.example.demo.model.ProjectManagerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectManagerRepo extends JpaRepository<ProjectManagerProfile,Long> {

}
