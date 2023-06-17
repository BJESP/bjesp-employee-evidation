package com.example.demo.model;

import com.example.demo.security.StringAttributeConverter;

import javax.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "country", nullable = false)
    @Convert(converter = StringAttributeConverter.class)
    private String country;

    @Column(name = "city", nullable = false)
    @Convert(converter = StringAttributeConverter.class)
    private String city;

    @Column(name = "street", nullable = false)
    @Convert(converter = StringAttributeConverter.class)
    private String street;
    @Column(name = "streetNumber", nullable = false)
    @Convert(converter = StringAttributeConverter.class)
    private String streetNumber;


    public Address() {
    }

    public Address(String country, String city, String street, String streetNumber) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}
