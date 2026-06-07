package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JournalPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileKey; // La clé de l'image sur Amazon S3

    private String description; // La légende de la photo (Commentaire technique)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SiteJournal siteJournal;
}
