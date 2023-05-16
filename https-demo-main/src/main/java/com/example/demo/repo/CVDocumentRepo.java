package com.example.demo.repo;

import com.example.demo.model.CVDocument;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
public interface CVDocumentRepo extends JpaRepository<CVDocument, Long> {
}
