package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dto.project.AssignmentRequestDTO;
import com.gzouli.ERP.dto.project.EquipmentDTO;
import com.gzouli.ERP.dto.project.ProjectEquipmentDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface EquipmentService {
    List<EquipmentDTO> getAllEquipment();

    // Gestion du Stock Global
    EquipmentDTO createEquipment(EquipmentDTO dto);

    @Transactional
    EquipmentDTO updateEquipment(Long id, EquipmentDTO dto);

    @Transactional
    void deleteEquipment(Long id);

    List<EquipmentDTO> getAvailableEquipment(); // Pour le select

    // Gestion par Projet
    List<ProjectEquipmentDTO> getProjectEquipment(Long projectId);
    void assignToProject(Long projectId, AssignmentRequestDTO request);
    void releaseFromProject(Long projectId, Long assignmentId, LocalDate returnDate);
}
