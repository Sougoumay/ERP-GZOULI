package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.project.TaskCreationDTO;
import com.gzouli.ERP.dto.project.TaskDTO;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.service.project.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class ProjectTaskController {
    private final TaskService taskService;

    // 1. Lister les tâches
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getProjectTasks(projectId));
    }

    // 2. Créer une tâche
    @PostMapping
    public ResponseEntity<?> createTask(
            @PathVariable("projectId") Long projectId,
            @RequestBody @Valid TaskCreationDTO dto) {
        try {
            TaskDTO created = taskService.createTask(projectId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Modifier une tâche (Label ou Assigné)
    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            @RequestBody @Valid TaskCreationDTO dto) {
        try {
            return ResponseEntity.ok(taskService.updateTask(taskId, dto));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Action rapide : Check / Uncheck (Terminer tâche)
    @PatchMapping("/{taskId}/toggle")
    public ResponseEntity<TaskDTO> toggleTask(@PathVariable("projectId") Long projectId, @PathVariable("taskId") Long taskId) {
        return ResponseEntity.ok(taskService.toggleTaskStatus(taskId));
    }

    // 5. Supprimer une tâche
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("projectId") Long projectId, @PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // 6. Bonus : Obtenir le % d'avancement global
    @GetMapping("/progress")
    public ResponseEntity<Map<String, Double>> getProgress(@PathVariable("projectId") Long projectId) {
        double progress = taskService.getProjectProgress(projectId);
        Map<String, Double> response = new HashMap<>();
        response.put("progress", progress);
        return ResponseEntity.ok(response);
    }
}
