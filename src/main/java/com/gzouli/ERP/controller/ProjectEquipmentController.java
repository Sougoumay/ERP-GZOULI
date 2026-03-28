package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.project.AssignmentRequestDTO;
import com.gzouli.ERP.dto.project.ProjectEquipmentDTO;
import com.gzouli.ERP.service.project.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/equipments")
@RequiredArgsConstructor
public class ProjectEquipmentController {

    private final EquipmentService equipmentService;

    // 1. Lister le matériel du projet (Historique + Actif)
    @GetMapping
    public ResponseEntity<List<ProjectEquipmentDTO>> getProjectEquipment(@PathVariable Long projectId) {
        return ResponseEntity.ok(equipmentService.getProjectEquipment(projectId));
    }

    // 2. Affecter du matériel (Prendre du stock)
    @PostMapping
    public ResponseEntity<?> assignEquipment(
            @PathVariable("projectId") Long projectId,
            @RequestBody @Valid AssignmentRequestDTO request) {
        try {
            equipmentService.assignToProject(projectId, request);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Erreur métier (Déjà pris)
        }
    }

    // 3. Libérer du matériel (Rendre au stock)
    // PATCH car on modifie juste la date de fin
    @PatchMapping("/{assignmentId}/release")
    public ResponseEntity<?> releaseEquipment(
            @PathVariable("projectId") Long projectId,
            @PathVariable("assignmentId") Long assignmentId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        try {
            equipmentService.releaseFromProject(projectId, assignmentId, returnDate);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Erreur date invalide
        }
    }
}
