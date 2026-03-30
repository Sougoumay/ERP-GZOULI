package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dao.*;
import com.gzouli.ERP.dto.project.JournalPhotoResponseDTO;
import com.gzouli.ERP.dto.project.SiteJournalDetailDTO;
import com.gzouli.ERP.dto.project.SiteJournalRequestDTO;
import com.gzouli.ERP.dto.project.SiteJournalSummaryDTO;
import com.gzouli.ERP.entity.*;
import com.gzouli.ERP.service.FileStorageService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteJournalServiceImpl implements SiteJournalService {

    private final SiteJournalRepository siteJournalRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;

    public SiteJournalServiceImpl(SiteJournalRepository siteJournalRepository,
                                  ProjectRepository projectRepository,
                                  EmployeeRepository employeeRepository, FileStorageService fileStorageService) {
        this.siteJournalRepository = siteJournalRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    @Override
    public void createJournal(Long projectId, SiteJournalRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        Employee authorToAssign;

        // Si l'Admin dépose pour quelqu'un d'autre (Gestion du retard)
        if (dto.getAuthorId() != null) {
            authorToAssign = employeeRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        } else {
            // Saisie normale : on récupère l'auteur via son Token JWT
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            authorToAssign = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        }

        SiteJournal journal = new SiteJournal();
        journal.setProject(project);
        journal.setAuthor(authorToAssign);
        journal.setWorkDate(dto.getWorkDate());
        journal.setLocation(dto.getLocation());
        journal.setTaskDescription(dto.getTaskDescription());

        // Ajout des PVs
        if (dto.getPvFileKeys() != null) {
            journal.setPvFileKeys(dto.getPvFileKeys());
        }

        // Ajout des Photos légendées
        if (dto.getPhotos() != null) {
            List<JournalPhoto> photos = dto.getPhotos().stream().map(pDto -> {
                JournalPhoto photo = new JournalPhoto();
                photo.setFileKey(pDto.getFileKey());
                photo.setDescription(pDto.getDescription());
                photo.setSiteJournal(journal); // Lien bidirectionnel Hibernate
                return photo;
            }).collect(Collectors.toList());

            journal.setPhotos(photos);
        }

        siteJournalRepository.save(journal);
    }

    @Override
    public List<SiteJournalSummaryDTO> getJournalsByProject(Long projectId) {
        List<SiteJournal> journals = siteJournalRepository.findByProjectIdOrderByWorkDateDesc(projectId);

        return journals.stream().map(journal -> {
            SiteJournalSummaryDTO dto = new SiteJournalSummaryDTO();
            dto.setId(journal.getId());
            dto.setWorkDate(journal.getWorkDate());
            dto.setLocation(journal.getLocation());

            if (journal.getAuthor() != null) {
                dto.setAuthorName(journal.getAuthor().getFirstName() + " " + journal.getAuthor().getLastName().toUpperCase());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SiteJournalDetailDTO getJournalDetails(Long journalId) {
        SiteJournal journal = siteJournalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Compte rendu introuvable"));

        SiteJournalDetailDTO dto = new SiteJournalDetailDTO();
        dto.setId(journal.getId());
        dto.setWorkDate(journal.getWorkDate());
        dto.setLocation(journal.getLocation());
        dto.setTaskDescription(journal.getTaskDescription());

        if (journal.getAuthor() != null) {
            dto.setAuthorName(journal.getAuthor().getFirstName() + " " + journal.getAuthor().getLastName().toUpperCase());
        }

        // Conversion des clés S3 des PVs en URLs lisibles
        List<String> pvUrls = new ArrayList<>();
        if (journal.getPvFileKeys() != null) {
            for (String key : journal.getPvFileKeys()) {
                pvUrls.add(fileStorageService.generatePresignedUrl(key)); // Utilisez votre méthode existante S3
            }
        }
        dto.setPvFileUrls(pvUrls);

        // Conversion des clés S3 des Photos en URLs avec leurs descriptions
        List<JournalPhotoResponseDTO> photoDtos = new ArrayList<>();
        if (journal.getPhotos() != null) {
            for (JournalPhoto photo : journal.getPhotos()) {
                JournalPhotoResponseDTO pDto = new JournalPhotoResponseDTO();
                pDto.setFileUrl(fileStorageService.generatePresignedUrl(photo.getFileKey()));
                pDto.setDescription(photo.getDescription());
                photoDtos.add(pDto);
            }
        }
        dto.setPhotos(photoDtos);

        return dto;
    }
}
