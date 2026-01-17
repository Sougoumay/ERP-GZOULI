package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private Double salary;

    // Historique des équipements (PC, Voiture, Téléphone...) assignés à cet employé
//    @OneToMany(mappedBy = "employee")
//    @ToString.Exclude // Sécurité anti-boucle infinie
//    private List<EquipmentAssignment> equipmentAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude
    private List<SalaryAdvance> advances;

    // 2. Gestion des Tâches (L'opérationnel)
    // "Quelles sont les taches que je dois faire ?"
    @OneToMany(mappedBy = "assignee")
    @ToString.Exclude
    private List<Task> assignedTasks = new ArrayList<>();

    // 3. Gestion du Suivi de Projet (Le management)
    // "Quels sont les chantiers que je supervise ?"
    // On utilise ManyToMany car un ingénieur suit plusieurs chantiers
    // et un chantier peut avoir plusieurs ingénieurs.
    @ManyToMany
    @JoinTable(
            name = "employee_monitored_projects",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    @ToString.Exclude
    private List<Project> monitoredProjects = new ArrayList<>();

    // 4. Les Comptes Rendus écrits par cet employé
    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    private List<SiteJournal> journals = new ArrayList<>();

//    public List<Equipment> getActiveEquipments() {
//        if (equipmentAssignments == null) return new ArrayList<>();
//        return equipmentAssignments.stream()
//                .filter(a -> a.getEndDate() == null) // Seulement ceux actifs
//                .map(EquipmentAssignment::getEquipment)
//                .toList();
//    }
}