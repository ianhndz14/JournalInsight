package com.journalinsight.backend.dto;

public class RegisterRequest {

    private String firstName;
    private String lastNamePaternal;
    private String email;
    private String password;
    private String phoneNumber;
    private String accountType;   // "P" = Patient, "R" = Professional
    private String specialty;     // only for professionals

    public RegisterRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastNamePaternal() { return lastNamePaternal; }
    public void setLastNamePaternal(String lastNamePaternal) { this.lastNamePaternal = lastNamePaternal; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
