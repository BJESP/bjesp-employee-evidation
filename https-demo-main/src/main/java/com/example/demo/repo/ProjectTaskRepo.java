package com.example.demo.repo;

import com.example.demo.model.ProjectTask;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
public interface ProjectTaskRepo extends JpaRepository<ProjectTask, Long> {
}
