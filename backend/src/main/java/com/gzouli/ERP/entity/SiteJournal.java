package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class SiteJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate workDate; // Date de l'intervention

    private String location; // Le lieu (Ex: CT Tilouguite)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String taskDescription; // Ce qui a été fait

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private Employee author; // Technician or Engineer

    // Liste des clés S3 pour les PVs (L'ancienne variable pdfFilePath est remplacée par cette liste)
    @ElementCollection
    @CollectionTable(name = "site_journal_pvs", joinColumns = @JoinColumn(name = "site_journal_id"))
    @Column(name = "pv_file_key")
    private List<String> pvFileKeys = new ArrayList<>();

    // Les photos de chantier avec leurs légendes
    @OneToMany(mappedBy = "siteJournal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalPhoto> photos = new ArrayList<>();
}
