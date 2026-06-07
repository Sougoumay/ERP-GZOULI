package com.gzouli.ERP.dto.employee;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String idCardNumber;
    private String address;
    private String phoneNumber;
    private Double salary;
    private LocalDate birthday;
    private String role;
    private boolean active;
}
