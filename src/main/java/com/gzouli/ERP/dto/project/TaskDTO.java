package com.gzouli.ERP.dto.project;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDTO {
    private Long id;
    private String label;
    private float taskWeight;
    private Boolean completed;
    private LocalDate completionDate;
    private LocalDate startDate;
    private LocalDate scheduledEndDate;

    // Infos de l'assigné (pour affichage direct dans le tableau)
    private Long assigneeId;
    private String assigneeName;
}
