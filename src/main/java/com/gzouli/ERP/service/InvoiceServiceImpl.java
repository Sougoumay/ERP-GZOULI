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

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final ProjectRepository projectRepository;
    private final InvoiceRepository invoiceRepository;
    private final FileStorageService fileStorageService;

    @Override
    public void addInvoiceToProject(Long projectId, InvoiceRegistrationDTO dto, MultipartFile file) {
        String s3Key = null;

        try {
            // 1. Upload S3 (Hors Transaction)
            s3Key = fileStorageService.uploadFile(file, "invoices");

            System.out.println("La clé unique de l'objet est " + s3Key);

            // 2. Save BDD
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(dto.getInvoiceNumber());
            invoice.setSubmissionDate(dto.getSubmissionDate());
            invoice.setAmount(dto.getAmount());
            invoice.setIsCertified(dto.getIsCertified());
            invoice.setProject(project);
            invoice.setS3Key(s3Key);
            invoice.setFileName(file.getOriginalFilename());

            invoiceRepository.save(invoice);

        } catch (Exception e) {
            // 3. Rollback Manuel S3 si erreur
            if (s3Key != null) fileStorageService.deleteFile(s3Key);
            throw new RuntimeException("Erreur enregistrement facture", e);
        }
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
    public void updateInvoice(Long invoiceId, InvoiceRegistrationDTO dto, MultipartFile file) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable"));

        // Mise à jour des champs texte
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setSubmissionDate(dto.getSubmissionDate());
        invoice.setAmount(dto.getAmount());
        // On ne touche pas à isCertified ici si on veut le gérer à part,
        // ou bien on le met à jour si le formulaire le permet.

        // Gestion du fichier S3 (Seulement si un nouveau fichier est envoyé)
        if (file != null && !file.isEmpty()) {
            // 1. Supprimer l'ancien fichier S3
            if (invoice.getS3Key() != null) {
                fileStorageService.deleteFile(invoice.getS3Key());
            }
            // 2. Upload du nouveau
            String newKey = fileStorageService.uploadFile(file, "invoices");
            invoice.setS3Key(newKey);
            invoice.setFileName(file.getOriginalFilename());
        }

        invoiceRepository.save(invoice);
    }

    @Override
    public void toggleCertification(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable"));

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
                System.err.println("Warning: Fichier S3 orphelin " + s3Key);
            }
        }
    }
}
