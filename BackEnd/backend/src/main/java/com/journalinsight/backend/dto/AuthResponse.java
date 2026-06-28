package com.journalinsight.backend.dto;

public class AuthResponse {

    private String phoneNumber;
    private String accountType;   // "P" = Patient, "R" = Professional
    private String firstName;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String phoneNumber, String accountType, String firstName, String message) {
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
        this.firstName   = firstName;
        this.message     = message;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
