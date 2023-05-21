package com.example.demo.repo;

import com.example.demo.model.ProjectTask;
import com.example.demo.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Scope("singleton")
public interface ProjectTaskRepo extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> getAllProjectTasksByEngineerProfileId(Long byEmail);
}
