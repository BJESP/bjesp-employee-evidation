package com.example.demo.utils;

import com.example.demo.dto.AddEngineerToProjectDTO;
import com.example.demo.dto.ProjectManagerUpdateDTO;
import com.example.demo.dto.UpdateProjectTaskDTO;
import com.example.demo.model.Address;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserValidation extends  GeneralValidation{


    UserValidation(){

    }


    public boolean validEditProjectManagerDTO(ProjectManagerUpdateDTO projectManagerUpdateDTO){
        if(!validAddress(projectManagerUpdateDTO.getAddress())|| !validPhoneNumber(projectManagerUpdateDTO.getPhoneNumber()))
            return false;
        return true;
    }

    public boolean validAddEngineerToProject(AddEngineerToProjectDTO addEngineerToProjectDTO){
        if(!ThrowIfEndBeforeStart(addEngineerToProjectDTO.getStartDate(),addEngineerToProjectDTO.getEndDate()) ||
        !ThrowIfInPast(addEngineerToProjectDTO.getStartDate()) || !validTaskName(addEngineerToProjectDTO.getTaskName())||
        !validDescription(addEngineerToProjectDTO.getDescription()))
            return false;
        return true;

    }

    public boolean validChangeEngineer(UpdateProjectTaskDTO updateProjectTaskDTO){
        if(!ThrowIfEndBeforeStart(updateProjectTaskDTO.getStartDate(),updateProjectTaskDTO.getEndDate()) ||
                !ThrowIfInPast(updateProjectTaskDTO.getStartDate()) || !validTaskName(updateProjectTaskDTO.getTaskName())||
                !validDescription(updateProjectTaskDTO.getDescription()))
            return false;
        return true;
    }

    public boolean validAddress(Address address){
        if (!validStreetNumber(address.getStreetNumber()) || !ValidCity(address.getCity()) ||
                !ValidCountry(address.getCountry()) || ! ValidStreet(address.getStreet()))
            return false;

        return true;
    }


    public boolean validUserEmail(String userEmail) {
        if (userEmail.isBlank()) {
            System.out.println("Your email needs to be inserted!");
            return false;
        } else if (!IsProperEmail(userEmail)) {
            System.out.println("You have entered an invalid email address.");
            return false;
        } else if (IsTooLong(userEmail, 35)) {
            System.out.println("Your email shouldn't contain more than 35 characters!");
            return false;
        }
        return true;
    }

    public boolean validPassword(String password) {
        if (password.isBlank()) {
            System.out.println("Your password needs to be inserted!");
            return false;
        } else if (!HasLowercaseLetter(password)) {
            System.out.println("Your password should contain at least one lowercase letter.");
            return false;
        } else if (!HasUppercaseLetter(password)) {
            System.out.println("Your password should contain at least one uppercase letter.");
            return false;
        } else if (!HasNumber(password)) {
            System.out.println("Your password should contain at least one number.");
            return false;
        } else if (!HasSpecialCharacter(password)) {
            System.out.println("Your password should contain at least one special character.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(password)) {
            System.out.println("Your password shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(password)) {
            System.out.println("Your password shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(password, 10)) {
            System.out.println("Your password should contain at least 10 characters!");
            return false;
        } else if (IsTooLong(password, 30)) {
            System.out.println("Your password shouldn't contain more than 30 characters!");
            return false;
        }
        return true;
    }

    private boolean validFirstName(String firstName) {
        if (firstName.isBlank()) {
            System.out.println("Your first name needs to be inserted!");
            return false;
        } else if (HasNumber(firstName)) {
            System.out.println("Your first name shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(firstName)) {
            System.out.println("Your first name shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(firstName)) {
            System.out.println("Your first name shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(firstName)) {
            System.out.println("Your first name shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(firstName, 2)) {
            System.out.println("Your first name should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(firstName, 20)) {
            System.out.println("Your first name shouldn't contain more than 20 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(firstName)) {
            System.out.println("Your first name needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }
    private boolean validTaskName(String taskName) {
        if (taskName.isBlank()) {
            System.out.println("Your task name needs to be inserted!");
            return false;
        } else if (HasNumber(taskName)) {
            System.out.println("Your task name shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(taskName)) {
            System.out.println("Your task name shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(taskName)) {
            System.out.println("Your task name shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(taskName)) {
            System.out.println("Your task name shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(taskName, 2)) {
            System.out.println("Your task name should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(taskName, 20)) {
            System.out.println("Your task name shouldn't contain more than 20 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(taskName)) {
            System.out.println("Your task name needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }
    private boolean validDescription(String description) {
        if (description.isBlank()) {
            System.out.println("Your description needs to be inserted!");
            return false;
        } else if (HasNumber(description)) {
            System.out.println("Your description shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(description)) {
            System.out.println("Your description shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(description)) {
            System.out.println("Your description shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(description)) {
            System.out.println("Your description shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(description, 2)) {
            System.out.println("Your description should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(description, 20)) {
            System.out.println("Your description shouldn't contain more than 20 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(description)) {
            System.out.println("Your description needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }

    private boolean validLastName(String lastName) {
        if (lastName.isBlank()) {
            System.out.println("Your last name needs to be inserted!");
            return false;
        } else if (HasNumber(lastName)) {
            System.out.println("Your last name shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(lastName)) {
            System.out.println("Your last name shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(lastName)) {
            System.out.println("Your last name shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(lastName)) {
            System.out.println("Your last name shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(lastName, 2)) {
            System.out.println("Your last name should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(lastName, 35)) {
            System.out.println("Your last name shouldn't contain more than 35 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(lastName)) {
            System.out.println("Your last name needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }

    private boolean ValidCity(String city){

        if (city.isBlank()) {
            System.out.println("Your city needs to be inserted!");
            return false;
        } else if (HasNumber(city)) {
            System.out.println("Your city shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(city)) {
            System.out.println("Your city shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(city)) {
            System.out.println("Your city shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(city)) {
            System.out.println("Your city shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(city, 2)) {
            System.out.println("Your city should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(city, 35)) {
            System.out.println("Your city shouldn't contain more than 35 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(city)) {
            System.out.println("Your city needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }
    private boolean ValidCountry(String country){

        if (country.isBlank()) {
            System.out.println("Your country needs to be inserted!");
            return false;
        } else if (HasNumber(country)) {
            System.out.println("Your country shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(country)) {
            System.out.println("Your country shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(country)) {
            System.out.println("Your country shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(country)) {
            System.out.println("Your country shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(country, 2)) {
            System.out.println("Your country should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(country, 35)) {
            System.out.println("Your country shouldn't contain more than 35 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(country)) {
            System.out.println("Your country needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }
    private boolean ValidStreet(String street){

        if (street.isBlank()) {
            System.out.println("Your street needs to be inserted!");
            return false;
        } else if (HasNumber(street)) {
            System.out.println("Your street shouldn't contain numbers.");
            return false;
        } else if (HasSpecialCharacter(street)) {
            System.out.println("Your street shouldn't contain special characters.");
            return false;
        } else if (HasLessOrGreaterThanCharacter(street)) {
            System.out.println("Your street shouldn't contain special character < or >.");
            return false;
        } else if (HasSpace(street)) {
            System.out.println("Your street shouldn't contain spaces!");
            return false;
        } else if (IsTooShort(street, 5)) {
            System.out.println("Your street should contain at least 2 characters!");
            return false;
        } else if (IsTooLong(street, 35)) {
            System.out.println("Your street shouldn't contain more than 35 characters!");
            return false;
        } else if (!HasUppercaseLetterAtStartOnly(street)) {
            System.out.println("Your street needs to have one uppercase letter at the start!");
            return false;
        }
        return true;
    }

    private boolean validStreetNumber(String streetNumber) {
        int number = Integer.parseInt(streetNumber);
        if (streetNumber.isBlank()) {
            System.out.println("Your street number needs to be inserted!");
            return false;

        }
        else if (number > 100){
            System.out.println("Street number should be less than 100 ");
            return false;
        }
        else if (number < 0){
            System.out.println("Street number should be positive number");
            return false;
        }
        return true;
    }

    private boolean validPhoneNumber(String phoneNumber) {
        if (phoneNumber.isBlank()) {
            System.out.println("Your phone number needs to be inserted!");
            return false;
        } else if (HasSpace(phoneNumber)) {
            System.out.println("Your phone number shouldn't contain spaces!");
            return false;
        } else if (HasLowercaseLetter(phoneNumber) || HasUppercaseLetter(phoneNumber)) {
            System.out.println("Your phone number shouldn't contain letters!");
            return false;
        } else if (!Pattern.compile("^[+]*[(]{0,1}[0-9]{1,3}[)]{0,1}[-\\s./0-9]*").matcher(phoneNumber).matches()) {
            System.out.println("Your phone number is not in right form!");
            return false;
        }
        return true;
    }
    public boolean ThrowIfEndBeforeStart(LocalDate start, LocalDate end)
    {
        if (start.compareTo(end) >= 0)
        {
            return false;
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
