package com.gzouli.ERP.dto.invoice;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceRegistrationDTO {
    private String invoiceNumber;
    private LocalDate submissionDate;
    private Double amount;
    private Boolean isCertified;
}
