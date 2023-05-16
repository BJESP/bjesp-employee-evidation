package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="PasswordlessToken")
@Getter @Setter
public class PasswordlessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed; //isUsed = true if someone entered it once

    public PasswordlessToken() {

    }


    public boolean IsStillValid()
    {
        boolean valid = true;

        if(creationTime.isBefore(LocalDateTime.now().minusMinutes(3)) || isUsed == true)
        {
            valid = false;
        }

        return valid;
    }

    public PasswordlessToken(String username) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.creationTime = LocalDateTime.now();
        this.isUsed = false;
    }

    // Getters and setters

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
