package com.gzouli.ERP.dto;

import com.gzouli.ERP.enums.Role;

import java.time.LocalDate;

public class EmployeeDTO {
    // Champs pour Cognito
    private String email;
    private String firstName;
    private String lastName;

    // Champs Métiers (Local DB)
    private String phoneNumber;
    private String idCardNumber; // CIN
    private String address;
    private Double salary;
    private LocalDate birthday;
    private Role role;
}
