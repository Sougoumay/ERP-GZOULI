package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label; // PC, Printer
    private LocalDate purchaseDate;

    // On peut avoir l'historique complet ici
    @ToString.Exclude
    @OneToMany(mappedBy = "equipment")
    private List<EquipmentAssignment> assignments;

    // Méthode utilitaire pour trouver qui a l'équipement "maintenant"
    public EquipmentAssignment getCurrentAssignment() {
        if (assignments == null) return null;
        return assignments.stream()
                .filter(a -> a.getEndDate() == null)
                .findFirst()
                .orElse(null);
    }
}
