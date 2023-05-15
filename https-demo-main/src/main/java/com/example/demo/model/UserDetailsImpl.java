package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;



    public UserDetailsImpl(Long id, String username, String email, String password
                           ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;

    }

    public static UserDetailsImpl build(User user) {


        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
                );
    }



    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }


}
