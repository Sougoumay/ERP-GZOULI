package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ProjectMissionOrderRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.project.ProjectMissionOrderRequestDTO;
import com.gzouli.ERP.dto.project.ProjectMissionOrderResponseDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.ProjectMissionOrder;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import com.gzouli.ERP.service.FileStorageService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionOrderServiceImpl implements MissionOrderService {
    private final ProjectMissionOrderRepository projectMissionOrderRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService s3Service; // Votre service de fichier

    public MissionOrderServiceImpl(ProjectMissionOrderRepository projectMissionOrderRepository, ProjectRepository projectRepository, EmployeeRepository employeeRepository, FileStorageService s3Service) {
        this.projectMissionOrderRepository = projectMissionOrderRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    @Override
    public ProjectMissionOrderResponseDTO createOrder(Long projectId, ProjectMissionOrderRequestDTO dto) throws IllegalArgumentException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        // Extraction de l'email de l'utilisateur connecté via le Token JWT Cognito [1]
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        // Vérification de sécurité métier : Fichier obligatoire en création
        if (dto.getFileKey() == null || dto.getFileKey().isEmpty()) {
            throw new IllegalArgumentException("Le document justificatif est obligatoire pour un nouvel ordre.");
        }

        ProjectMissionOrder order = new ProjectMissionOrder();
        order.setProject(project);
        order.setUploadedBy(employee);
        order.setType(dto.getType());
        order.setEffectiveDate(dto.getEffectiveDate());
        order.setFileKey(dto.getFileKey());

        order = projectMissionOrderRepository.save(order);

        // Confirmation du fichier sur S3 (Ajout du tag ou déplacement)
        s3Service.confirmFile(dto.getFileKey());

        return mapToResponseDTO(order);
    }

    // --- 2. READ (Liste) ---
    @Override
    public List<ProjectMissionOrderResponseDTO> getOrdersByProject(Long projectId) {
        List<ProjectMissionOrder> orders = projectMissionOrderRepository.findProjectMissionByProjectIdOrderByEffectiveDate(projectId);
        return orders.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    // --- 3. UPDATE ---
    @Transactional
    @Override
    public ProjectMissionOrderResponseDTO updateOrder(Long orderId, ProjectMissionOrderRequestDTO dto) {
        ProjectMissionOrder order = projectMissionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre introuvable"));

        order.setType(dto.getType());
        order.setEffectiveDate(dto.getEffectiveDate());

        // Le fichier est optionnel en update. On le met à jour seulement s'il y a un nouveau fichier
        if (dto.getFileKey() != null && !dto.getFileKey().isEmpty()) {
            s3Service.deleteFile(order.getFileKey());
            order.setFileKey(dto.getFileKey());
            s3Service.confirmFile(dto.getFileKey()); // Confirmer le nouveau
        }

        order = projectMissionOrderRepository.save(order);
        return mapToResponseDTO(order);
    }

    // --- 4. DELETE ---
    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        ProjectMissionOrder order = projectMissionOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre introuvable"));

        // Optionnel mais recommandé : Supprimer le fichier d'AWS S3
        if (order.getFileKey() != null) {
            try {
                // s3Service.deleteFile(order.getFileKey()); 
            } catch(Exception e) {
                System.err.println("Impossible de supprimer le fichier S3 : " + e.getMessage());
            }
        }
        projectMissionOrderRepository.delete(order);
    }

    // --- UTILITAIRE DE MAPPING ---
    private ProjectMissionOrderResponseDTO mapToResponseDTO(ProjectMissionOrder order) {
        ProjectMissionOrderResponseDTO dto = new ProjectMissionOrderResponseDTO();
        dto.setId(order.getId());
        dto.setType(order.getType());
        dto.setEffectiveDate(order.getEffectiveDate());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getUploadedBy() != null) {
            dto.setUploadedBy(order.getUploadedBy().getFirstName() + " " + order.getUploadedBy().getLastName());
        }

        // Génération du lien S3 dynamique pour le bouton "Œil" du Frontend !
        if (order.getFileKey() != null) {
            dto.setFileUrl(s3Service.generatePresignedUrl(order.getFileKey()));
        }

        return dto;
    }

    @Transactional
    @Override
    public long calculateEffectiveDays(Long projectId) {
        // 1. Récupérer l'historique chronologique des ordres du projet
        List<ProjectMissionOrder> orders = projectMissionOrderRepository.findProjectMissionByIdOrderByEffectiveDateAsc(projectId);

        if (orders == null || orders.isEmpty()) {
            return 0; // Aucun ordre (même pas de démarrage), donc 0 jour effectif
        }

        long totalEffectiveDays = 0;
        boolean isActive = false;
        LocalDate currentPeriodStart = null;

        // 2. Parcourir les ordres pour calculer les intervalles
        for (ProjectMissionOrder order : orders) {
            switch (order.getType()) {

                case DEMARRAGE:
                case REPRISE:
                    // On allume le compteur uniquement s'il n'était pas déjà allumé
                    if (!isActive) {
                        isActive = true;
                        currentPeriodStart = order.getEffectiveDate();
                    }
                    break;

                case ARRET:
                    // On éteint le compteur et on calcule le temps écoulé depuis le dernier démarrage/reprise
                    if (isActive && currentPeriodStart != null) {
                        totalEffectiveDays += ChronoUnit.DAYS.between(currentPeriodStart, order.getEffectiveDate());
                        isActive = false;
                        currentPeriodStart = null;
                    }
                    break;
            }
        }

        // 3. Gestion du temps "En cours" (Aujourd'hui)
        // Si la boucle s'est terminée et que le projet est toujours Actif (Pas d'arrêt final)
        if (isActive && currentPeriodStart != null) {
            LocalDate today = LocalDate.now();

            // Sécurité : on ne compte pas si la date d'effet est dans le futur
            if (today.isAfter(currentPeriodStart)) {
                totalEffectiveDays += ChronoUnit.DAYS.between(currentPeriodStart, today);
            }
        }

        return totalEffectiveDays;
    }
}
