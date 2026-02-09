package com.gzouli.ERP.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskCreationDTO {
    @NotBlank(message = "Le libellé de la tâche est obligatoire")
    private String label;

    private Long assigneeId; // Optionnel : On peut créer une tâche sans l'assigner tout de suite
}
