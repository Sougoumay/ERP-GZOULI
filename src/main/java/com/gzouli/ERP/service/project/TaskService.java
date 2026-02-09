package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dto.project.TaskCreationDTO;
import com.gzouli.ERP.dto.project.TaskDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getProjectTasks(Long projectId);
    TaskDTO createTask(Long projectId, TaskCreationDTO dto);
    TaskDTO updateTask(Long taskId, TaskCreationDTO dto);
    void deleteTask(Long taskId);
    TaskDTO toggleTaskStatus(Long taskId); // Bascule (Fait / Non Fait)

    // Bonus : KPI d'avancement
    double getProjectProgress(Long projectId);
}
