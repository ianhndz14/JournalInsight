package com.journalinsight.backend.dto;

public class PatientDto {

    private String phoneNumber;
    private String firstName;
    private String lastNamePaternal;

    public PatientDto() {}

    public PatientDto(String phoneNumber, String firstName, String lastNamePaternal) {
        this.phoneNumber      = phoneNumber;
        this.firstName        = firstName;
        this.lastNamePaternal = lastNamePaternal;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastNamePaternal() { return lastNamePaternal; }
    public void setLastNamePaternal(String lastNamePaternal) { this.lastNamePaternal = lastNamePaternal; }
}
