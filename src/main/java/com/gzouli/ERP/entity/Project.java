package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description; // Objet du marché
    @Column(columnDefinition = "TEXT")
    private String generalObjectives;
    @Column(columnDefinition = "TEXT")
    private String specificObjectives;
    private Double amountIncTax; // TTC
    private Double amountExTax;  // HT
    private Integer durationMonths;

    @ToString.Exclude
    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @ToString.Exclude
    @OneToMany(mappedBy = "project")
    private List<Invoice> invoices;

    // On ne pointe plus vers Equipment directement, mais vers l'historique des assignations
    @ToString.Exclude
    @OneToMany(mappedBy = "project")
    private List<EquipmentAssignment> equipmentAssignments = new ArrayList<>();

    // --- NOUVEAU : Liste des superviseurs (Ingénieurs/Chefs) qui suivent ce projet ---
    @ManyToMany(mappedBy = "monitoredProjects")
    @ToString.Exclude
    private List<Employee> supervisors = new ArrayList<>();

    // --- NOUVEAU : Liste des journaux de chantier (Comptes rendus) ---
    @OneToMany(mappedBy = "project")
    @ToString.Exclude
    private List<SiteJournal> siteJournals = new ArrayList<>();

    // --- METHODE UTILITAIRE (Bonus) ---

    /**
     * Méthode pratique pour récupérer uniquement les équipements
     * ACTUELLEMENT sur le chantier (ceux dont la date de fin est null).
     * Utile pour l'affichage frontend sans faire de requête complexe.
     */
    public List<Equipment> getActiveEquipments() {
        if (equipmentAssignments == null) return new ArrayList<>();

        return equipmentAssignments.stream()
                .filter(a -> a.getEndDate() == null) // On garde ceux qui sont actifs
                .map(EquipmentAssignment::getEquipment) // On extrait l'objet Equipement
                .collect(Collectors.toList());
    }
}