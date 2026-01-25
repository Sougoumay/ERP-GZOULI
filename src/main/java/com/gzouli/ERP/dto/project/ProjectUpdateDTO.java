package com.gzouli.ERP.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectUpdateDTO {
    private String name;
    private String description;
    private String generalObjectives;
    private String specificObjectives;
    private Double amountIncTax;
    private Double amountExTax;
    private Integer durationMonths;
    private LocalDate startDate;
}
