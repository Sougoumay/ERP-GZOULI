package com.gzouli.ERP.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreationDTO {
    @NotBlank(message = "Le libellé de la tâche est obligatoire")
    private String label;
    private float taskWeight;
    private LocalDate startDate;
    private LocalDate scheduledEndDate;


    private Long assigneeId; // Optionnel : On peut créer une tâche sans l'assigner tout de suite
}
