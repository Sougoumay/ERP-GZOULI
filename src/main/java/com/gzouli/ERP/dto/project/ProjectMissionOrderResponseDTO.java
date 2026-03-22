package com.gzouli.ERP.dto.project;

import com.gzouli.ERP.enums.MissionOrderType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectMissionOrderResponseDTO {
    private Long id;
    private MissionOrderType type;
    private LocalDate effectiveDate;
    private LocalDateTime createdAt;

    private String uploadedBy; // Nom et Prénom de l'employé qui a ajouté l'ordre
    private String fileUrl;    // Le lien S3 généré pour visualiser le PDF
}
