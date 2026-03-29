package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.InvoiceRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.invoice.InvoiceDTO;
import com.gzouli.ERP.dto.invoice.InvoiceRegistrationDTO;
import com.gzouli.ERP.entity.Invoice;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public void addInvoiceToProject(Long projectId, InvoiceRegistrationDTO dto) {

            // 1. Save BDD
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(dto.getInvoiceNumber());
            invoice.setSubmissionDate(dto.getSubmissionDate());
            invoice.setAmount(dto.getAmount());
            invoice.setIsCertified(dto.getIsCertified());
            invoice.setProject(project);
            invoice.setS3Key(dto.getFileKey());
            invoice.setFileName(dto.getFileName());

            invoiceRepository.save(invoice);

            fileStorageService.confirmFile(dto.getFileKey());
    }

    @Override
    public List<InvoiceDTO> getInvoicesByProject(Long projectId) {
        return invoiceRepository.findByProjectId(projectId).stream()
                .map(inv -> {
                    InvoiceDTO dto = new InvoiceDTO();
                    dto.setId(inv.getId());
                    dto.setInvoiceNumber(inv.getInvoiceNumber());
                    dto.setSubmissionDate(inv.getSubmissionDate());
                    dto.setAmount(inv.getAmount());
                    dto.setIsCertified(inv.getIsCertified());
                    dto.setFileName(inv.getFileName());
                    // Génération URL fraîche à la volée
                    dto.setDownloadUrl(fileStorageService.generatePresignedUrl(inv.getS3Key()));
                    return dto;
                }).toList();
    }

    @Override
    public void updateInvoice(Long invoiceId, InvoiceRegistrationDTO dto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable"));

        // Mise à jour des champs texte
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setSubmissionDate(dto.getSubmissionDate());
        invoice.setAmount(dto.getAmount());

        if(!invoice.getIsCertified() && dto.getIsCertified()) {
            invoice.setSubmissionDate(LocalDate.now());
        }

        if(!dto.getFileKey().isEmpty() && !dto.getFileKey().isBlank()) {
            fileStorageService.deleteFile(invoice.getS3Key());
            invoice.setS3Key(dto.getFileKey());
            invoice.setFileName(dto.getFileName());

            invoiceRepository.save(invoice);
            fileStorageService.confirmFile(dto.getFileKey());
        }

        invoiceRepository.save(invoice);
    }

    @Transactional
    @Override
    public void toggleCertification(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable"));

        if(!invoice.getIsCertified()) {
            invoice.setCertificationDate(LocalDate.now());
        }

        // Inversion du booléen
        invoice.setIsCertified(!invoice.getIsCertified());
        invoiceRepository.save(invoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable"));

        String s3Key = invoice.getS3Key();

        // Suppression BDD d'abord (Sécurité transactionnelle)
        invoiceRepository.delete(invoice);
        invoiceRepository.flush(); // Force l'exécution SQL

        // Suppression S3
        if (s3Key != null) {
            try {
                fileStorageService.deleteFile(s3Key);
            } catch (Exception e) {
                log.warn("Fichier S3 orphelin détecté : {}", s3Key);
            }
        }
    }
}
