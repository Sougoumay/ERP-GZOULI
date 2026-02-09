package com.gzouli.ERP.dto.project;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectEquipmentDTO {
    private Long assignmentId; // ID de la liaison (pour pouvoir la supprimer/clôturer)
    private Long equipmentId;
    private String label;
    private String reference;
    private LocalDate startDate; // Date d'arrivée sur chantier
    private LocalDate endDate;   // Date de départ (si null = encore présent)
    private String status;
}
