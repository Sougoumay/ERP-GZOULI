package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class EquipmentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY) // Un assignment concerne obligatoirement un équipement
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // --- La période ---

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate; // Si NULL = Toujours actif

    // Validation pour éviter les dates incohérentes
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalStateException("La date de fin ne peut pas être avant la date de début.");
        }
    }
}