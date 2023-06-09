package com.example.demo.repo;

import com.example.demo.model.CVDocument;
import com.example.demo.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Scope("singleton")
public interface CVDocumentRepo extends JpaRepository<CVDocument, Long>
{

    CVDocument findByEngineerProfile(Optional<User> user);
}
