package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.project.*;
import com.gzouli.ERP.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    // CREATE
    @PostMapping
    public ResponseEntity<ProjectSummaryDTO> createProject(@Valid @RequestBody ProjectRegistrationDTO dto) {
        ProjectSummaryDTO created = projectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // READ ONE (Detail)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailDTO> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProjectSummaryDTO> updateProject(@PathVariable("id") Long id,
                                                           @RequestBody ProjectSummaryDTO dto) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    // SOFT DELETE / ENABLE
    // Exemple : PUT /api/projects/1/status?active=false
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleStatus(@PathVariable("id") Long id, @RequestParam("active") boolean active) {
        projectService.toggleProjectStatus(id, active);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/team")
    public ResponseEntity<Void> addTeamMembers(@PathVariable("id") Long id, @RequestBody List<Long> employeeIds) {
        System.out.println("L'ajout d'un elt est en cours pour ces id" + employeeIds.toString());
        projectService.assignSupervisor(id, employeeIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/team")
    public ResponseEntity<List<TeamMemberDTO>> getProjectTeam(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectTeam(id));
    }


    @PostMapping("/{id}/team/remove")
    public ResponseEntity<Void> removeTeamMembers(@PathVariable("id") Long id, @RequestBody List<Long> employeeIds) {
        projectService.removeSupervisor(id, employeeIds);
        return ResponseEntity.noContent().build();
    }
}


