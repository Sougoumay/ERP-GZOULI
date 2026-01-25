package com.gzouli.ERP.dto.employee;

import com.gzouli.ERP.enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeDetailDTO {
    private Long id;
    private String cognitoId;
    private String email;
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private String address;
    private LocalDate birthday;
    private Double salary; // Donnée sensible incluse ici

    // On ne renvoie pas toute l'entité Car, juste ce qu'il faut
    private String currentCarModel;
    private String currentCarPlate;

    private List<String> activeProjectNames;
}
