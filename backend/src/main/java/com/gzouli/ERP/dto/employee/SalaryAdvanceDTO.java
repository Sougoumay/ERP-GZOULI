package com.gzouli.ERP.dto.employee;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SalaryAdvanceDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Double amount;
    private LocalDate date;
    private String note;
}
