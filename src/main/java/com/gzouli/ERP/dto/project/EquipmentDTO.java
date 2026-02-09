package com.gzouli.ERP.dto.project;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EquipmentDTO {
    private Long id;
    private String label;
    private String reference;
    private String state;
    private boolean available;
    private LocalDate purchaseDate;
}
