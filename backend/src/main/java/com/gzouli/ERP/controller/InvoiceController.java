package com.gzouli.ERP.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gzouli.ERP.dto.invoice.InvoiceDTO;
import com.gzouli.ERP.dto.invoice.InvoiceRegistrationDTO;
import com.gzouli.ERP.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<?> addInvoice(
            @PathVariable("projectId") Long projectId,
            @RequestBody InvoiceRegistrationDTO dto    // Le fichier PDF
    ) {

        try {
            invoiceService.addInvoiceToProject(projectId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur technique lors de l'enregistrement de la facture."); // 500 Internal Server Error
        }
    }

    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getInvoices(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByProject(projectId));
    }

    @PutMapping(value = "/{invoiceId}")
    public ResponseEntity<?> updateInvoice(
            @PathVariable("projectId") Long projectId,
            @PathVariable("invoiceId") Long invoiceId,
            @RequestBody InvoiceRegistrationDTO dto
    ) {
        try {
            invoiceService.updateInvoice(invoiceId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la modification");
        }
    }

    // TOGGLE CERTIFICATION (Action rapide)
    @PatchMapping("/{invoiceId}/certify")
    public ResponseEntity<?> toggleCertify(@PathVariable("projectId") Long projectId, @PathVariable("invoiceId") Long invoiceId) {
        invoiceService.toggleCertification(invoiceId);
        return ResponseEntity.ok().build();
    }

    // SUPPRESSION FACTURE
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<?> deleteInvoice(@PathVariable("projectId") Long projectId, @PathVariable("invoiceId") Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }
}
