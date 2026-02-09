package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dao.TaskRepository;
import com.gzouli.ERP.dto.project.TaskCreationDTO;
import com.gzouli.ERP.dto.project.TaskDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.Task;
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
        return taskRepository.findByProjectId(projectId).stream()
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
        task.setProject(project);
        task.setIsCompleted(false); // Par défaut

        // Gestion de l'assignation (si fournie)
        if (dto.getAssigneeId() != null) {
            Employee assignee = employeeRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

            // Règle métier optionnelle : Vérifier si l'employé est actif ?
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

        // Mise à jour de l'assignation
        if (dto.getAssigneeId() != null) {
            // Si on change d'assigné
            if (task.getAssignee() == null || !task.getAssignee().getId().equals(dto.getAssigneeId())) {
                Employee newAssignee = employeeRepository.findById(dto.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));
                task.setAssignee(newAssignee);
            }
        } else {
            // Si on retire l'assignation (null envoyé)
            task.setAssignee(null);
        }

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDTO toggleTaskStatus(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        boolean newState = !Boolean.TRUE.equals(task.getIsCompleted());
        task.setIsCompleted(newState);

        // Si complété -> Date du jour, sinon -> null
        task.setCompletionDate(newState ? LocalDate.now() : null);

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Tâche introuvable");
        }
        taskRepository.deleteById(taskId);
    }

    @Override
    public double getProjectProgress(Long projectId) {
        long total = taskRepository.countByProjectId(projectId);
        if (total == 0) return 0.0;

        long completed = taskRepository.countByProjectIdAndIsCompletedTrue(projectId);
        return (double) completed / total * 100.0;
    }

    // --- Mapper ---
    private TaskDTO mapToDTO(Task t) {
        TaskDTO dto = new TaskDTO();
        dto.setId(t.getId());
        dto.setLabel(t.getLabel());
        dto.setCompleted(Boolean.TRUE.equals(t.getIsCompleted()));
        dto.setCompletionDate(t.getCompletionDate());

        if (t.getAssignee() != null) {
            dto.setAssigneeId(t.getAssignee().getId());
            dto.setAssigneeName(t.getAssignee().getFirstName() + " " + t.getAssignee().getLastName());
        }
        return dto;
    }
}
