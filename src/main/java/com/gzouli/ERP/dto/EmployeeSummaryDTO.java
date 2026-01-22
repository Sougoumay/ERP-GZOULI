package com.gzouli.ERP.dto;

import lombok.Data;

@Data
public class EmployeeSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
    private boolean status;
}
