package com.example.demo.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BlockedUser {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="email")
    String email;

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime blockedUntil;

    @Transient
    private boolean isBlocked;

    public boolean isBlocked() {
        return getBlockedUntil().isAfter(LocalDateTime.now()); // this is generic implementation, you can always make it timezone specific
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

}
