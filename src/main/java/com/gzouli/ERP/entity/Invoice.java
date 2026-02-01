package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate submissionDate;
    private Boolean isCertified; // Status approved for payment

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String s3Key;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    private Project project;
}
