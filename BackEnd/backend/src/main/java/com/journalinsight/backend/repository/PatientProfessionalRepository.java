package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.PatientProfessional;
import com.journalinsight.backend.model.PatientProfessionalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientProfessionalRepository extends JpaRepository<PatientProfessional, PatientProfessionalId> {

    @Query("SELECT pp FROM PatientProfessional pp WHERE pp.id.professionalId = :professionalId")
    List<PatientProfessional> findByProfessionalId(@Param("professionalId") String professionalId);

    @Query("SELECT pp FROM PatientProfessional pp WHERE pp.id.patientId = :patientId")
    List<PatientProfessional> findByPatientId(@Param("patientId") String patientId);
}
