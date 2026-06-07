package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand; // Ex: Dacia, Renault

    @Column(nullable = false)
    private String model; // Ex: Logan, Duster

    @Column(unique = true, nullable = false)
    private String registrationNumber; // Matricule (Ex: 12345-A-6)

    // Charge Fixe (Crédit ou Location)
    private Double monthlyCost;

    // État (EN_SERVICE, EN_PANNE...)
    private String state = "EN_SERVICE";

    // Historique des conducteurs
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CarAssignment> assignments = new ArrayList<>();
}
