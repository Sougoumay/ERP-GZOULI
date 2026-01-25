package com.gzouli.ERP.dto.employee;

import com.gzouli.ERP.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
//@Getter
//@Setter
public class EmployeeRegistrationDTO {
    // Champs pour Cognito
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    private String email;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String lastName;

    // Champs Métiers (Local DB)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phoneNumber;

    @NotBlank(message = "Le numéro de CIN est obligatoire")
    private String idCardNumber; // CIN

    private String address;

    @NotNull(message = "Le salaire de base est obligatoire")
    @PositiveOrZero(message = "Le salaire ne peut pas être négatif")
    private Double salary;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthday;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
