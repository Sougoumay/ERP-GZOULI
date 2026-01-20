package com.gzouli.ERP.dto;

import com.gzouli.ERP.enums.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
//@Getter
//@Setter
public class EmployeeRegistrationDTO {
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
