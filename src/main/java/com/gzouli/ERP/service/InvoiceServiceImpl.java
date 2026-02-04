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
import java.util.Optional;

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
}
