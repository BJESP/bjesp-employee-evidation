package com.example.demo.model;

import com.example.demo.dto.RegistrationDTO;
import com.example.demo.security.StringAttributeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="USERS")
@Getter @Setter
public class User implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(name = "password")
    @Convert(converter = StringAttributeConverter.class)
    private String password;

    @Column(name = "first_name")
    @Convert(converter = StringAttributeConverter.class)
    private String firstName;

    @Column(name = "last_name")
    @Convert(converter = StringAttributeConverter.class)
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @Convert(converter = StringAttributeConverter.class)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column
    @Convert(converter = StringAttributeConverter.class)
    private String phoneNumber;

    @Column
    @Convert(converter = StringAttributeConverter.class)
    private String title;

    @Column
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
    @Column
    private LocalDate dateOfEmployment;

    @Column
    private boolean changedPassword;
    @Column
    private boolean initialAdmin;



    public void setUsername(String username) {
        this.username = username;
    }



    public boolean isInitialAdmin() {
        return initialAdmin;
    }

    public void setInitialAdmin(boolean initialAdmin) {
        this.initialAdmin = initialAdmin;
    }

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }



    public User(RegistrationDTO userRegistrationDTO) {
        this.setFirstName(userRegistrationDTO.getFirstName());
        this.setLastName(userRegistrationDTO.getLastName());
        this.setEmail(userRegistrationDTO.getEmail());
        this.setTitle(userRegistrationDTO.getTitle());
        this.setAddress(new Address(userRegistrationDTO.getCountry(), userRegistrationDTO.getCity(), userRegistrationDTO.getStreet(), userRegistrationDTO.getStreetNumber()));
        this.setPhoneNumber(userRegistrationDTO.getPhoneNumber());
        this.setPassword(userRegistrationDTO.getPassword());
        this.setActive(false);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public LocalDate getDateOfEmployment() {
        return dateOfEmployment;
    }

    public void setDateOfEmployment(LocalDate dateOfEmployment) {
        this.dateOfEmployment = dateOfEmployment;
    }

    public boolean isChangedPassword() {
        return changedPassword;
    }

    public void setChangedPassword(boolean changedPassword) {
        this.changedPassword = changedPassword;
    }
}
