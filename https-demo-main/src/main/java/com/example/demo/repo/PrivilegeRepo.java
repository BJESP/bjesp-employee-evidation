package com.example.demo.repo;

import com.example.demo.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege,Long> {

}
