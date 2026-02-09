package com.gzouli.ERP.dto.project;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDTO {
    private Long id;
    private String label;
    private Boolean completed;
    private LocalDate completionDate;

    // Infos de l'assigné (pour affichage direct dans le tableau)
    private Long assigneeId;
    private String assigneeName;
}
