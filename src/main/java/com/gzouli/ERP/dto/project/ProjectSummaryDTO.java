package com.gzouli.ERP.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private String description; // Objet du marché
    private Double amountIncTax;
    private boolean active;
    private LocalDate startDate;
    private Integer durationMonths;
}
