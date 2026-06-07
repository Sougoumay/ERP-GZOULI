package com.gzouli.ERP.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentRequestDTO {
    @NotNull(message = "L'équipement est obligatoire")
    private Long equipmentId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;
}
