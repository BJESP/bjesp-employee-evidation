package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private User user;

    Collection<GrantedAuthority> authorities=null;





   /* public static UserDetailsImpl build(User user) {


        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
                );
    }*/

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(User user, Set<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    public void setAuthorities(Collection<GrantedAuthority> authorities)
    {
        this.authorities=authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
