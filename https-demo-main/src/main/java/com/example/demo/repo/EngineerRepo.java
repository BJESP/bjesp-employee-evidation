package com.example.demo.repo;

import com.example.demo.model.EngineerProfile;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
@Scope("singleton")
public interface EngineerRepo extends JpaRepository<EngineerProfile,Long> {


}
