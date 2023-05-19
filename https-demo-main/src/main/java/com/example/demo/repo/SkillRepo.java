package com.example.demo.repo;

import com.example.demo.model.Skill;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
public interface SkillRepo extends JpaRepository<Skill, Long> {
    Skill findByEngineerProfileEmailAndName(String engineerProfileEmail, String name);
}
