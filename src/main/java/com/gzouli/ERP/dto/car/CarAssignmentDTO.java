package com.gzouli.ERP.dto.car;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CarAssignmentDTO {
    @NotNull
    private Long carId;
    @NotNull
    private Long employeeId;
    @NotNull
    private LocalDate startDate;
}


