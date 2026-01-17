package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class SiteJournal { // Journal de chantier [cite: 15]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate workDate;
    @Column(columnDefinition = "TEXT")
    private String taskDescription;
    private String pdfFilePath; // Unique compiled PDF
    private String serviceType; // DESIGN, SUPERVISION, or BOTH

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private Project project;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id") // Renommé pour plus de clarté en BDD (sinon employee_id)
    @ToString.Exclude
    private Employee author; // Technician or Engineer [cite: 35, 40]
}
