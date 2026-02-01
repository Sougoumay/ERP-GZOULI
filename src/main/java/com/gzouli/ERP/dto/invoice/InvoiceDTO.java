package com.gzouli.ERP.dto.invoice;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDate submissionDate;
    private Double amount;
    private Boolean isCertified;

    private String fileName; // Nom du fichier
    private String downloadUrl; // L'URL signée temporaire (générée à la volée)
}
