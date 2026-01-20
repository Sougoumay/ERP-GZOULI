package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class EquipmentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false) // Un assignment concerne obligatoirement un équipement
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // --- La période ---

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate; // Si NULL = Toujours actif

    // --- Validation pour garantir l'exclusivité ---
//    @PrePersist
//    @PreUpdate
//    private void validate() {
//        if (project != null && employee != null) {
//            throw new IllegalStateException("Un équipement ne peut pas être assigné à un projet ET un employé en même temps.");
//        }
//        if (project == null && employee == null) {
//            throw new IllegalStateException("Une assignation doit avoir une cible (Projet ou Employé).");
//        }
//    }
}