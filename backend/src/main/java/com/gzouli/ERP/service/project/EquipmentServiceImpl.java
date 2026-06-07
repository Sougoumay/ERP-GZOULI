package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dao.EquipmentAssignmentRepository;
import com.gzouli.ERP.dao.EquipmentRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.project.AssignmentRequestDTO;
import com.gzouli.ERP.dto.project.EquipmentDTO;
import com.gzouli.ERP.dto.project.ProjectEquipmentDTO;
import com.gzouli.ERP.entity.Equipment;
import com.gzouli.ERP.entity.EquipmentAssignment;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;

    // =========================================================
    // PARTIE 1 : GESTION DU STOCK (INVENTAIRE GLOBAL)
    // =========================================================

    @Override
    public List<EquipmentDTO> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EquipmentDTO createEquipment(EquipmentDTO dto) {
        // Validation : Unicité de la référence (ex: code barre)
        if (dto.getReference() != null && !dto.getReference().isEmpty()) {
            // Idéalement, ajoutez une méthode existsByReference dans le Repository
            // if (equipmentRepository.existsByReference(dto.getReference())) ...
        }

        Equipment equipment = new Equipment();
        equipment.setLabel(dto.getLabel());
        equipment.setReference(dto.getReference());
        equipment.setPurchaseDate(dto.getPurchaseDate()); // Assurez-vous d'avoir ajouté ce champ au DTO
        equipment.setState(dto.getState());

        Equipment saved = equipmentRepository.save(equipment);
        return mapToDTO(saved);
    }

    @Transactional
    @Override
    public EquipmentDTO updateEquipment(Long id, EquipmentDTO dto) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel introuvable"));

        equipment.setLabel(dto.getLabel());
        equipment.setReference(dto.getReference());
        equipment.setPurchaseDate(dto.getPurchaseDate());
        equipment.setState(dto.getState());

        Equipment updated = equipmentRepository.save(equipment);
        return mapToDTO(updated);
    }

    @Transactional
    @Override
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matériel introuvable"));

        // Sécurité : Impossible de supprimer si le matériel a déjà été utilisé (Historique)
        if (!equipment.getAssignments().isEmpty()) {
            throw new BusinessException("Impossible de supprimer ce matériel : il est lié à un historique de projets.");
        }

        equipmentRepository.delete(equipment);
    }

    // =========================================================
    // PARTIE 2 : AFFECTATION PROJET (Déjà fait précédemment)
    // =========================================================

    @Override
    public List<EquipmentDTO> getAvailableEquipment() {
        return equipmentRepository.findAvailableEquipment().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectEquipmentDTO> getProjectEquipment(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        return project.getEquipmentAssignments().stream()
                .map(this::mapToProjectEquipmentDTO)
                // On peut trier pour mettre les actifs en premier
                .sorted((a, b) -> {
                    if (a.getEndDate() == null && b.getEndDate() != null) return -1;
                    return 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignToProject(Long projectId, AssignmentRequestDTO request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Matériel introuvable"));

        // 1. VERIFICATION CRITIQUE : Le matériel est-il déjà ailleurs ?
        // On cherche s'il existe une assignation sans date de fin pour cet équipement
        boolean isAlreadyAssigned = equipment.getAssignments().stream()
                .anyMatch(a -> a.getEndDate() == null);

        if (isAlreadyAssigned) {
            throw new BusinessException("Ce matériel (" + equipment.getLabel() + ") est déjà assigné à un autre projet !");
        }

        // 2. Création de l'assignation
        EquipmentAssignment assignment = new EquipmentAssignment();
        assignment.setProject(project);
        assignment.setEquipment(equipment);
        assignment.setStartDate(request.getStartDate());
        // endDate reste null = Actif

        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void releaseFromProject(Long projectId, Long assignmentId, LocalDate returnDate) {
        EquipmentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignation introuvable"));

        // Sécurité : Vérifier que l'assignation concerne bien ce projet
        if (!assignment.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Cette assignation n'appartient pas à ce projet.");
        }

        // 3. VALIDATION DATE : On ne peut pas rendre le matériel AVANT de l'avoir pris
        if (returnDate.isBefore(assignment.getStartDate())) {
            throw new IllegalArgumentException("La date de retour ne peut pas être antérieure à la date d'affectation (" + assignment.getStartDate() + ")");
        }

        assignment.setEndDate(returnDate);
        assignmentRepository.save(assignment);
    }

    // --- Mappers ---

    private EquipmentDTO mapToDTO(Equipment e) {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(e.getId());
        dto.setLabel(e.getLabel());
        dto.setReference(e.getReference());
        dto.setState(e.getState());
        dto.setPurchaseDate(e.getPurchaseDate()); // Ajouté

        // Calcul de la disponibilité :
        // Est disponible si AUCUNE assignation n'est en cours (endDate == null)
        boolean isBusy = e.getAssignments().stream().anyMatch(a -> a.getEndDate() == null);
        dto.setAvailable(!isBusy);

        return dto;
    }

    private ProjectEquipmentDTO mapToProjectEquipmentDTO(EquipmentAssignment a) {
        ProjectEquipmentDTO dto = new ProjectEquipmentDTO();
        dto.setAssignmentId(a.getId());
        dto.setEquipmentId(a.getEquipment().getId());
        dto.setLabel(a.getEquipment().getLabel());
        dto.setReference(a.getEquipment().getReference());
        dto.setStartDate(a.getStartDate());
        dto.setEndDate(a.getEndDate());
        dto.setStatus(a.getEndDate() == null ? "ACTIF" : "LIBÉRÉ");
        return dto;
    }

}
