package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label; // PC, Printer

    private String reference;
    private LocalDate purchaseDate;
    private String state;

    // On peut avoir l'historique complet ici
    @ToString.Exclude
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    private List<EquipmentAssignment> assignments = new ArrayList<>();
}
