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

    // Les 3 Historiques
    private List<ProjectHistoryDTO> projects;
    private List<CarHistoryDTO> vehicles;
    private List<SalaryAdvanceDTO> advances;

    @Data
    public static class ProjectHistoryDTO {
        private Long projectId;
        private String projectName;
        private String roleOnProject; // "Chef de mission" ou "Technicien"
        private LocalDate startDate;
        // private LocalDate endDate; (si géré)
    }

    @Data
    public static class CarHistoryDTO {
        private String carModel;
        private String registrationNumber;
        private LocalDate startDate;
        private LocalDate endDate; // null si cours
    }
}
