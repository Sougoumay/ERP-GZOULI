package com.gzouli.ERP.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectRegistrationDTO {
    @NotBlank(message = "Le nom du maitre d'ouvrage du projet est obligatoire")
    private String projectOwner;

    @NotBlank(message = "Le nom du projet est obligatoire")
    private String name;

    @NotBlank(message = "L'objet du marché (description) est obligatoire")
    private String description;

    private String generalObjectives;
    private String specificObjectives;

//    @NotNull(message = "Le montant TTC est obligatoire")
//    @Positive
    private Double amountIncTax;

    @NotNull(message = "Le montant HT est obligatoire")
    @Positive
    private Double amountExTax;

    @NotNull(message = "La durée (en mois) est obligatoire")
    @Positive
    private Integer durationMonths;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    private LocalDate projectWinDate;
}
