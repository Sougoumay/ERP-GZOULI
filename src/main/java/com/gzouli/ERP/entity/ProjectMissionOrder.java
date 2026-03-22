package com.gzouli.ERP.entity;
import com.gzouli.ERP.enums.MissionOrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ProjectMissionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionOrderType type; // DEMARRAGE, ARRET, REPRISE

    @Column(nullable = false)
    private LocalDate effectiveDate; // Date à laquelle l'ordre prend effet sur le chantier

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // Date exacte de l'upload (Générée automatiquement par Spring)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Employee uploadedBy; // Tracé via le Token JWT dans le backend

    @Column(nullable = false)
    private String fileKey; // Le lien S3 du fichier PDF obligatoire
}
