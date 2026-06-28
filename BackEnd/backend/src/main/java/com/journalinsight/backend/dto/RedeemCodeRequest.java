package com.journalinsight.backend.dto;

public class RedeemCodeRequest {

    private String professionalId;
    private String patientId;

    public RedeemCodeRequest() {}

    public String getProfessionalId() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId = professionalId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
}
