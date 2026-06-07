package com.gzouli.ERP.dto.car;

import lombok.Data;

@Data
public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private String registrationNumber;
    private Double monthlyCost;
    private String state;

    // Infos sur le conducteur actuel (s'il y en a un)
    private boolean assigned;
    private Long currentDriverId;
    private String currentDriverName;
}
