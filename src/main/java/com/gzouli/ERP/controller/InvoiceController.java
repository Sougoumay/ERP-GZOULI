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

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addInvoice(
            @PathVariable Long projectId,
            @RequestPart("invoice") String invoiceJson, // Le DTO en String JSON
            @RequestPart("file") MultipartFile file     // Le fichier PDF
    ) {



        try {
            // Conversion manuelle du String JSON en Objet Java
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Pour gérer LocalDate
            InvoiceRegistrationDTO dto = objectMapper.readValue(invoiceJson, InvoiceRegistrationDTO.class);

            System.out.println("Le service d'ajout des factures est appelés");
            invoiceService.addInvoiceToProject(projectId, dto, file);

            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            // Cas 1 : Le JSON envoyé par le front est invalide (ex: date mal formatée, virgule manquante)
            e.printStackTrace(); // Log pour le développeur
            return ResponseEntity.badRequest()
                    .body("Erreur de format : Les données de la facture sont invalides."); // 400 Bad Request

        } catch (Exception e) {
            // Cas 2 : Erreur serveur générale (ex: S3 ne répond pas, BDD plantée)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur technique lors de l'enregistrement de la facture."); // 500 Internal Server Error
        }
    }

    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getInvoices(@PathVariable Long projectId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByProject(projectId));
    }
}
