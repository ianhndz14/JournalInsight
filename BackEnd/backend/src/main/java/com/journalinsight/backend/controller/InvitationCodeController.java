package com.journalinsight.backend.controller;

import com.journalinsight.backend.dto.PatientDto;
import com.journalinsight.backend.dto.RedeemCodeRequest;
import com.journalinsight.backend.service.InvitationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeService;

    public InvitationCodeController(InvitationCodeService invitationCodeService) {
        this.invitationCodeService = invitationCodeService;
    }

    // POST /api/invitations/redeem  body: { "professionalId": "7875555678", "patientId": "7875551234" }
    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(@RequestBody RedeemCodeRequest request) {
        try {
            String message = invitationCodeService.linkPatientToProfessional(
                    request.getProfessionalId(), request.getPatientId()
            );
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/invitations/professionals?patientId=7875551234
    @GetMapping("/professionals")
    public ResponseEntity<List<PatientDto>> getProfessionalsForPatient(@RequestParam String patientId) {
        return ResponseEntity.ok(invitationCodeService.getProfessionalsForPatient(patientId));
    }

    // DELETE /api/invitations/unlink?patientId=7875551234&professionalId=7875555678
    @DeleteMapping("/unlink")
    public ResponseEntity<?> unlink(
            @RequestParam String patientId,
            @RequestParam String professionalId) {
        try {
            invitationCodeService.unlinkPatientFromProfessional(patientId, professionalId);
            return ResponseEntity.ok(Map.of("message", "Unlinked successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
