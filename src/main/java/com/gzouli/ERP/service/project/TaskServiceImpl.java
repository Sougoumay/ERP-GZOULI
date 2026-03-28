package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dao.TaskRepository;
import com.gzouli.ERP.dto.project.TaskCreationDTO;
import com.gzouli.ERP.dto.project.TaskDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.Task;
import com.gzouli.ERP.enums.Role;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<TaskDTO> getProjectTasks(Long projectId) {
        // Vérif existence projet
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Projet introuvable");
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        return taskRepository.findByProjectId(projectId).stream()
                .filter(task -> (!Role.TECHNICIEN.equals(employee.getRole())) || 
                        (task.getAssignee() != null && task.getAssignee().getId().equals(employee.getId())))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDTO createTask(Long projectId, TaskCreationDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        Task task = new Task();
        task.setLabel(dto.getLabel());
        task.setWeight(dto.getTaskWeight());
        task.setStartDate(dto.getStartDate());
        task.setScheduledEndDate(dto.getScheduledEndDate());
        task.setProject(project);
        task.setIsCompleted(false); // Par défaut

        // Gestion de l'assignation (si fournie)
        if (dto.getAssigneeId() != null) {
            Employee assignee = employeeRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

            if (!assignee.isActive()) {
                throw new BusinessException("Impossible d'assigner une tâche à un employé inactif.");
            }
            task.setAssignee(assignee);
        }

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long taskId, TaskCreationDTO dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        // Mise à jour du libellé
        task.setLabel(dto.getLabel());
        task.setWeight(dto.getTaskWeight());
        task.setStartDate(dto.getStartDate());
        task.setScheduledEndDate(dto.getScheduledEndDate());

        // Mise à jour de l'assignation
        if (dto.getAssigneeId() != null) {
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(dto.getAssigneeId())) {
                Employee newAssignee = employeeRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));
                task.setAssignee(newAssignee);
            }
        } else {
            task.setAssignee(null);
        }

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDTO toggleTaskStatus(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        verifyTaskAccess(task);

        boolean newState = !Boolean.TRUE.equals(task.getIsCompleted());
        task.setIsCompleted(newState);
        task.setCompletionDate(newState ? LocalDate.now() : null);

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));
        taskRepository.delete(task);
    }

    private void verifyTaskAccess(Task task) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        if (Role.TECHNICIEN.equals(employee.getRole())) {
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(employee.getId())) {
                throw new BusinessException("Accès refusé. Cette tâche ne vous est pas assignée.");
            }
        }
    }

    @Override
    public double getProjectProgress(Long projectId) {
        // 1. On récupère la somme totale des poids prévus pour ce projet
        double totalWeight = taskRepository.sumWeightsByProjectId(projectId);

        // Sécurité : Éviter la division par zéro s'il n'y a pas encore de tâches
        if (totalWeight == 0.0) {
            return 0.0;
        }

        // 2. On récupère la somme des poids des tâches terminées
        double completedWeight = taskRepository.sumCompletedWeightsByProjectId(projectId);

        // 3. Calcul du pourcentage d'avancement
        return (completedWeight / totalWeight) * 100.0;
    }

    // --- Mapper ---
    private TaskDTO mapToDTO(Task t) {
        TaskDTO dto = new TaskDTO();
        dto.setId(t.getId());
        dto.setLabel(t.getLabel());
        if(t.getWeight() != null) dto.setTaskWeight(t.getWeight());
        dto.setStartDate(t.getStartDate());
        dto.setScheduledEndDate(t.getScheduledEndDate());
        dto.setCompleted(Boolean.TRUE.equals(t.getIsCompleted()));
        dto.setCompletionDate(t.getCompletionDate());

        if (t.getAssignee() != null) {
            dto.setAssigneeId(t.getAssignee().getId());
            dto.setAssigneeName(t.getAssignee().getFirstName() + " " + t.getAssignee().getLastName());
        }
        return dto;
    }
}
