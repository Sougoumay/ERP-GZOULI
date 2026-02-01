package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.invoice.InvoiceDTO;
import com.gzouli.ERP.dto.project.*;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
     private final EmployeeRepository employeeRepository; // Pour plus tard

    @Override
    public ProjectSummaryDTO createProject(ProjectRegistrationDTO dto) {
        // 1. Validation Métier
        if (projectRepository.existsByName(dto.getName())) {
            throw new BusinessException("Un projet avec ce nom existe déjà.");
        }

        // 2. Mapping DTO -> Entity
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setGeneralObjectives(dto.getGeneralObjectives());
        project.setSpecificObjectives(dto.getSpecificObjectives());
        project.setAmountIncTax(dto.getAmountIncTax());
        project.setAmountExTax(dto.getAmountExTax());
        project.setDurationMonths(dto.getDurationMonths());
        project.setStartDate(dto.getStartDate());
        project.setActive(true); // Actif par défaut

        Project saved = projectRepository.save(project);
        return mapToSummary(saved);
    }

    @Override
    public ProjectSummaryDTO updateProject(Long id, ProjectSummaryDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + id));

        // Mise à jour partielle (seulement si le champ n'est pas null)
        if (dto.getName() != null) project.setName(dto.getName());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getAmountIncTax() != null) project.setAmountIncTax(dto.getAmountIncTax());
        if (dto.getAmountExTax() != null) project.setAmountExTax(dto.getAmountExTax());
        if (dto.getDurationMonths() != null) project.setDurationMonths(dto.getDurationMonths());
        if (dto.getGeneralObjectives() != null) project.setGeneralObjectives(dto.getGeneralObjectives());
        if (dto.getSpecificObjectives() != null) project.setSpecificObjectives(dto.getSpecificObjectives());
        if (dto.getStartDate() != null) project.setStartDate(dto.getStartDate());

        Project updated = projectRepository.save(project);
        return mapToSummary(updated);
    }

    @Override
    public ProjectDetailDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + id));

        return mapToDetail(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleProjectStatus(Long id, boolean isActive) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + id));

        project.setActive(isActive);
        projectRepository.save(project);
    }

    // --- Méthodes non implémentées pour le moment (Placeholders) ---
    @Override
    public void assignSupervisor(Long pId, List<Long> eIds) {
        Project project = projectRepository.findById(pId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        List<Employee> employeesToAdd = employeeRepository.findAllById(eIds);

        if (employeesToAdd.size() != eIds.size()) {
            throw new BusinessException("Certains employés spécifiés n'existent pas.");
        }

        List<Employee> currentTeam = project.getSupervisors();

        for (Employee emp : employeesToAdd) {
            if (!currentTeam.contains(emp)) {
                currentTeam.add(emp);
            }

//            }
        }
        project.setSupervisors(currentTeam);

        // 5. Sauvegarde unique
        projectRepository.save(project);
    }

    @Override
    public void removeSupervisor(Long pId, List<Long> eIds) {
        Project project = projectRepository.findById(pId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        List<Employee> employeesToRemove = employeeRepository.findAllById(eIds);

        if(project.getSupervisors().removeAll(employeesToRemove)) {
            projectRepository.save(project);
        } else {
            throw new BusinessException("Cet employé ne fait pas partie de ce projet.");
        }
    }

    @Override
    public List<TeamMemberDTO> getProjectTeam(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        List<TeamMemberDTO> dtos = new ArrayList<>();

        if (project.getSupervisors() != null) {
            dtos.addAll(project.getSupervisors().stream()
                    .map(s -> new TeamMemberDTO(s.getId(), s.getFirstName(), s.getLastName(), s.getRole(), s.getEmail()))
                    .toList());
        }
        return dtos;
    }

    @Override public Double calculateProjectProfitability(Long pId) { return 0.0; }
    @Override
    public void addInvoice(Long pId, InvoiceDTO iDto) {}
    @Override public MonthlyReportDataDTO getMonthlyReportData(Long pId, int m, int y) { return null; }

    // --- Helpers de Mapping ---

    private ProjectSummaryDTO mapToSummary(Project p) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setGeneralObjectives(p.getGeneralObjectives());
        dto.setSpecificObjectives(p.getSpecificObjectives());
        dto.setAmountIncTax(p.getAmountIncTax());
        dto.setAmountExTax(p.getAmountExTax());
        dto.setDurationMonths(p.getDurationMonths());
        dto.setStartDate(p.getStartDate());
        dto.setActive(p.isActive());
        return dto;
    }

    private ProjectDetailDTO mapToDetail(Project p) {
        ProjectDetailDTO dto = new ProjectDetailDTO();
        // Copie des champs Summary
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setAmountIncTax(p.getAmountIncTax());
        dto.setDurationMonths(p.getDurationMonths());
        dto.setStartDate(p.getStartDate());
        dto.setActive(p.isActive());

        // Champs Détails
        dto.setAmountExTax(p.getAmountExTax());
        dto.setGeneralObjectives(p.getGeneralObjectives());
        dto.setSpecificObjectives(p.getSpecificObjectives());

        // Mapping des superviseurs (Ingénieurs assignés)
        // Attention : nécessite que la relation ManyToMany soit bien chargée
        if (p.getSupervisors() != null) {
            dto.setTeamMembers(p.getSupervisors().stream()
                    .map(s -> new TeamMemberDTO(s.getId(), s.getFirstName(), s.getLastName(), s.getRole(), s.getEmail()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
