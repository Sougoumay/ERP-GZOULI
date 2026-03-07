package com.gzouli.ERP.dto.project;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectSummaryDTO {
    private Long id; // Optionnel (null à la création)

    @NotBlank(message = "Le nom du maitre d'ouvrage du projet est obligatoire")
    @Size(max = 255, message = "Le nom du maitre d'ouvrage ne doit pas dépasser 255 caractères")
    private String projectOwner;

    @NotBlank(message = "L'intitulé du projet est obligatoire")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères")
    private String name;

    @NotBlank(message = "L'objet du marché est obligatoire")
    private String description;

    // --- Champs Optionnels (Pas de @NotBlank) ---
    private String generalObjectives;

    private String specificObjectives; // Ajouté comme demandé

    @PositiveOrZero(message = "Le montant ne peut pas être négatif")
    private Double amountIncTax;

    @NotNull(message = "Le montant HT est obligatoire")
    @PositiveOrZero(message = "Le montant HT ne peut pas être négatif")
    private Double amountExTax;

    private boolean active;

    @NotNull(message = "La date de démarrage est obligatoire")
    private LocalDate startDate;

    private LocalDate projectWinDate;

    @NotNull(message = "Le délai est obligatoire")
    @Min(value = 1, message = "Le délai doit être d'au moins 1 mois")
    private Integer durationMonths;
}
