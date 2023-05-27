package com.example.demo.utils;

import com.example.demo.dto.*;
import com.example.demo.model.Address;
import com.example.demo.model.EngineerAccountDetailsDTO;
import com.example.demo.model.ValidationResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;
@Component
public class UserValidation extends  GeneralValidation{


    UserValidation(){

    }

    public ValidationResult validUpdateEngineerAccountDetailsDTO(EngineerAccountDetailsDTO engineerAccountDetailsDTO)
    {
        try {
            validFirstName(engineerAccountDetailsDTO.getFirstName());
            validLastName(engineerAccountDetailsDTO.getFirstName());
            validUserEmail(engineerAccountDetailsDTO.getEmail());
            validUserEmail(engineerAccountDetailsDTO.getUsername());
            if(engineerAccountDetailsDTO.getPassword() != null)
            {
                if(!engineerAccountDetailsDTO.getPassword().equals(""))
                {
                    validPassword(engineerAccountDetailsDTO.getPassword());
                }
            }

            validAddress(engineerAccountDetailsDTO.getAddress());
            validPhoneNumber(engineerAccountDetailsDTO.getPhoneNumber());

            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public ValidationResult validUpdateProjectTaskRequestDTO(UpdateProjectTaskRequestDTO updateProjectTaskDTO)
    {
        try
        {
            validUserEmail(updateProjectTaskDTO.getUsername());
            validDescription(updateProjectTaskDTO.getDescription());

            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public ValidationResult validPasswordlessLoginDTO(PasswordlessLoginDTO passwordlessLoginDTO)
    {
        try {
            validUserEmail(passwordlessLoginDTO.getUsername());
            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public ValidationResult validEditProjectManagerDTO(ProjectManagerUpdateDTO projectManagerUpdateDTO){
        try {
            validAddress(projectManagerUpdateDTO.getAddress());
            validPhoneNumber(projectManagerUpdateDTO.getPhoneNumber());
            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    public ValidationResult validAddEngineerToProject(AddEngineerToProjectDTO addEngineerToProjectDTO){
        try {
            ThrowIfEndBeforeStart(addEngineerToProjectDTO.getStartDate(), addEngineerToProjectDTO.getEndDate());
                    ThrowIfInPast(addEngineerToProjectDTO.getStartDate()) ;
                    validTaskName(addEngineerToProjectDTO.getTaskName()) ;
                    validDescription(addEngineerToProjectDTO.getDescription());
            validTaskName(addEngineerToProjectDTO.getTaskName());
            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    public ValidationResult validChangeEngineer(UpdateProjectTaskDTO updateProjectTaskDTO){
        try {
            ThrowIfEndBeforeStart(updateProjectTaskDTO.getStartDate(), updateProjectTaskDTO.getEndDate());
            ThrowIfInPast(updateProjectTaskDTO.getStartDate()) ;
            validTaskName(updateProjectTaskDTO.getTaskName()) ;
            validDescription(updateProjectTaskDTO.getDescription());

            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    public ValidationResult validAddress(Address address){
        try {
            validStreetNumber(address.getStreetNumber());
            ValidCity(address.getCity());
            ValidCountry(address.getCountry());
            ValidStreet(address.getStreet());

            // Other validations...
            return new ValidationResult(true, "Validation successful");
        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    public boolean validRating(int rating)
    {
        if (rating > 5 || rating < 1)
        {
            throw new IllegalArgumentException("Your rating must be in range 1-5!");
        }

        return true;
    }

    public boolean validUserEmail(String userEmail) {
        if (userEmail.isBlank()) {
            throw new IllegalArgumentException("Your email needs to be inserted!");
            //return false;
        } else if (!IsProperEmail(userEmail)) {
            throw new IllegalArgumentException("You have entered an invalid email address.");
            //return false;
        } else if (IsTooLong(userEmail, 35)) {
            throw new IllegalArgumentException("Your email shouldn't contain more than 35 characters!");
            //return false;
        }
        return true;
    }

    public boolean validPassword(String password) {
        if (password.isBlank()) {
            throw new IllegalArgumentException("Your password needs to be inserted!");
            //return false;
        } else if (!HasLowercaseLetter(password)) {
            throw new IllegalArgumentException("Your password should contain at least one lowercase letter.");
            //return false;
        } else if (!HasUppercaseLetter(password)) {
            throw new IllegalArgumentException("Your password should contain at least one uppercase letter.");
            //return false;
        } else if (!HasNumber(password)) {
            throw new IllegalArgumentException("Your password should contain at least one number.");
            //return false;
        } else if (!HasSpecialCharacter(password)) {
            throw new IllegalArgumentException("Your password should contain at least one special character.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(password)) {
            throw new IllegalArgumentException("Your password shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(password)) {
            throw new IllegalArgumentException("Your password shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(password, 10)) {
            throw new IllegalArgumentException("Your password should contain at least 10 characters!");
            //return false;
        } else if (IsTooLong(password, 30)) {
            throw new IllegalArgumentException("Your password shouldn't contain more than 30 characters!");
            //return false;
        }
        return true;
    }

    private boolean validFirstName(String firstName) {
        if (firstName.isBlank()) {
            throw new IllegalArgumentException("Your first name needs to be inserted!");
            //return false;
        } else if (HasNumber(firstName)) {
            throw new IllegalArgumentException("Your first name shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(firstName)) {
            throw new IllegalArgumentException("Your first name shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(firstName)) {
            throw new IllegalArgumentException("Your first name shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(firstName)) {
            throw new IllegalArgumentException("Your first name shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(firstName, 2)) {
            throw new IllegalArgumentException("Your first name should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(firstName, 20)) {
            throw new IllegalArgumentException("Your first name shouldn't contain more than 20 characters!");
           // return false;
        } else if (!HasUppercaseLetterAtStartOnly(firstName)) {
            throw new IllegalArgumentException("Your first name needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }
    private boolean validTaskName(String taskName) {
        if (taskName.isBlank()) {
            throw new IllegalArgumentException("Your task name needs to be inserted!");
           // return false;
        } else if (HasNumber(taskName)) {
            throw new IllegalArgumentException("Your task name shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(taskName)) {
            throw new IllegalArgumentException("Your task name shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(taskName)) {
            throw new IllegalArgumentException("Your task name shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(taskName)) {
            throw new IllegalArgumentException("Your task name shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(taskName, 2)) {
            throw new IllegalArgumentException("Your task name should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(taskName, 20)) {
            throw new IllegalArgumentException("Your task name shouldn't contain more than 20 characters!");
            //return false;
        } else if (!HasUppercaseLetterAtStartOnly(taskName)) {
            throw new IllegalArgumentException("Your task name needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }
    private boolean validDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Your description needs to be inserted!");
            //return false;
        } else if (HasNumber(description)) {
            throw new IllegalArgumentException("Your description shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(description)) {
            throw new IllegalArgumentException("Your description shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(description)) {
            throw new IllegalArgumentException("Your description shouldn't contain special character < or >.");
            //return false;
        } else if (IsTooShort(description, 2)) {
            throw new IllegalArgumentException("Your description should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(description, 20)) {
            throw new IllegalArgumentException("Your description shouldn't contain more than 20 characters!");
            //return false;
        } else if (!HasUppercaseLetterAtStartOnly(description)) {
            throw new IllegalArgumentException("Your description needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }

    private boolean validLastName(String lastName) {
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("Your last name needs to be inserted!");
            //return false;
        } else if (HasNumber(lastName)) {
            throw new IllegalArgumentException("Your last name shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(lastName)) {
            throw new IllegalArgumentException("Your last name shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(lastName)) {
            throw new IllegalArgumentException("Your last name shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(lastName)) {
            throw new IllegalArgumentException("Your last name shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(lastName, 2)) {
            throw new IllegalArgumentException("Your last name should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(lastName, 35)) {
            throw new IllegalArgumentException("Your last name shouldn't contain more than 35 characters!");
            //return false;
        } else if (!HasUppercaseLetterAtStartOnly(lastName)) {
            throw new IllegalArgumentException("Your last name needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }

    private boolean ValidCity(String city){

        if (city.isBlank()) {
            throw new IllegalArgumentException("Your city needs to be inserted!");
            //return false;
        } else if (HasNumber(city)) {
            throw new IllegalArgumentException("Your city shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(city)) {
            throw new IllegalArgumentException("Your city shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(city)) {
            throw new IllegalArgumentException("Your city shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(city)) {
            throw new IllegalArgumentException("Your city shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(city, 2)) {
            throw new IllegalArgumentException("Your city should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(city, 35)) {
            throw new IllegalArgumentException("Your city shouldn't contain more than 35 characters!");
            //return false;
        } else if (!HasUppercaseLetterAtStartOnly(city)) {
            throw new IllegalArgumentException("Your city needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }
    private boolean ValidCountry(String country){

        if (country.isBlank()) {
            throw new IllegalArgumentException("Your country needs to be inserted!");
            //return false;
        } else if (HasNumber(country)) {
            throw new IllegalArgumentException("Your country shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(country)) {
            throw new IllegalArgumentException("Your country shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(country)) {
            throw new IllegalArgumentException("Your country shouldn't contain special character < or >.");
            //return false;
        } else if (HasSpace(country)) {
            throw new IllegalArgumentException("Your country shouldn't contain spaces!");
            //return false;
        } else if (IsTooShort(country, 2)) {
            throw new IllegalArgumentException("Your country should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(country, 35)) {
            throw new IllegalArgumentException("Your country shouldn't contain more than 35 characters!");
            //return false;
        } else if (!HasUppercaseLetterAtStartOnly(country)) {
            throw new IllegalArgumentException("Your country needs to have one uppercase letter at the start!");
            //return false;
        }
        return true;
    }
    private boolean ValidStreet(String street){

        if (street.isBlank()) {
            throw new IllegalArgumentException("Your street needs to be inserted!");
            //return false;
        } else if (HasNumber(street)) {
            throw new IllegalArgumentException("Your street shouldn't contain numbers.");
            //return false;
        } else if (HasSpecialCharacter(street)) {
            throw new IllegalArgumentException("Your street shouldn't contain special characters.");
            //return false;
        } else if (HasLessOrGreaterThanCharacter(street)) {
            throw new IllegalArgumentException("Your street shouldn't contain special character < or >.");
            //return false;
        } else if (IsTooShort(street, 5)) {
            throw new IllegalArgumentException("Your street should contain at least 2 characters!");
            //return false;
        } else if (IsTooLong(street, 35)) {
            throw new IllegalArgumentException("Your street shouldn't contain more than 35 characters!");
            //return false;
        }
        return true;
    }

    private boolean validStreetNumber(String streetNumber) {
        if (streetNumber.isBlank()) {
            throw new IllegalArgumentException("Your street number needs to be inserted!");
            //return false;

        }
        return true;
    }

    private boolean validPhoneNumber(String phoneNumber) {
        if (phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Your phone number needs to be inserted!");
            //return false;
        } else if (HasSpace(phoneNumber)) {
            throw new IllegalArgumentException("Your phone number shouldn't contain spaces!");
            //return false;
        } else if (HasLowercaseLetter(phoneNumber) || HasUppercaseLetter(phoneNumber)) {
            throw new IllegalArgumentException("Your phone number shouldn't contain letters!");
            //return false;
        } else if (!Pattern.compile("^[+]*[(]{0,1}[0-9]{1,3}[)]{0,1}[-\\s./0-9]*").matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Your phone number is not in right form!");
            //eturn false;
        }
        return true;
    }
    public boolean ThrowIfEndBeforeStart(LocalDate start, LocalDate end)
    {
        if (start.compareTo(end) >= 0)
        {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        return true;
    }

    private boolean ThrowIfInPast(LocalDate start)
    {
        if (start.compareTo(LocalDate.now().plusDays(1)) < 0)
        {
            return false;
        }
        return true;
    }
}
