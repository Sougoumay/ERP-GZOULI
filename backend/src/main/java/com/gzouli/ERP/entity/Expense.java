package com.gzouli.ERP.entity;

import com.gzouli.ERP.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate expenseDate; // Date de la dépense (Crucial pour le filtre mensuel)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType type; // GASOIL, MAINTENANCE...

    private String label; // Description libre (ex: "Vidange Ford Ranger")

    @Column(nullable = false)
    private Double amount; // Montant TTC

    // --- Gestion de la Preuve (Ticket/Reçu) ---
    @Column(nullable = false)
    private String s3Key;    // Clé S3 (Scan du ticket)
    private String fileName; // Nom du fichier

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    private Employee performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Car linkedCar;

}
