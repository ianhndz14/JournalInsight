package com.journalinsight.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_professional")
public class PatientProfessional {

    @EmbeddedId
    private PatientProfessionalId id;

    @ManyToOne
    @MapsId("patientId")
    @JoinColumn(name = "patient_id_pk_fk1")
    private Patient patient;

    @ManyToOne
    @MapsId("professionalId")
    @JoinColumn(name = "professional_id_pk_fk2")
    private Professional professional;

    public PatientProfessional() {}

    public PatientProfessionalId getId() { return id; }
    public void setId(PatientProfessionalId id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Professional getProfessional() { return professional; }
    public void setProfessional(Professional professional) { this.professional = professional; }
}
