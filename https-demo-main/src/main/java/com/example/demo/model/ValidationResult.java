package com.example.demo.model;

import javax.persistence.Entity;


public class ValidationResult {

    private boolean isValid;
    private String errorMessage;

    public ValidationResult() {
    }

    public ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
