package com.journalinsight.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PatientProfessionalId implements Serializable {

    @Column(name = "patient_id_pk_fk1")
    private String patientId;

    @Column(name = "professional_id_pk_fk2")
    private String professionalId;

    public PatientProfessionalId() {}

    public PatientProfessionalId(String patientId, String professionalId) {
        this.patientId      = patientId;
        this.professionalId = professionalId;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getProfessionalId() { return professionalId; }
    public void setProfessionalId(String professionalId) { this.professionalId = professionalId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientProfessionalId)) return false;
        PatientProfessionalId that = (PatientProfessionalId) o;
        return Objects.equals(patientId, that.patientId) &&
               Objects.equals(professionalId, that.professionalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, professionalId);
    }
}
