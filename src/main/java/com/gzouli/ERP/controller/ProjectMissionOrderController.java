package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.project.ProjectMissionOrderRequestDTO;
import com.gzouli.ERP.dto.project.ProjectMissionOrderResponseDTO;
import com.gzouli.ERP.service.project.MissionOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/orders")
public class ProjectMissionOrderController {

    private final MissionOrderService projectMissionOrderService;

    public ProjectMissionOrderController(MissionOrderService projectMissionOrderService) {
        this.projectMissionOrderService = projectMissionOrderService;
    }

    @PostMapping
    public ResponseEntity<ProjectMissionOrderResponseDTO> createOrder(
            @PathVariable Long projectId,
            @RequestBody ProjectMissionOrderRequestDTO dto) {
        ProjectMissionOrderResponseDTO response = projectMissionOrderService.createOrder(projectId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMissionOrderResponseDTO>> getProjectMissionOrders(
            @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(projectMissionOrderService.getOrdersByProject(projectId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ProjectMissionOrderResponseDTO> updateOrder(
            @PathVariable("projectId") Long projectId,
            @PathVariable("orderId") Long orderId,
            @RequestBody ProjectMissionOrderRequestDTO dto) {
        // (Optionnel) Vous pouvez vérifier ici que orderId appartient bien à projectId
        return ResponseEntity.ok(projectMissionOrderService.updateOrder(orderId, dto));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable("projectId") Long projectId,
            @PathVariable("orderId") Long orderId) {
        projectMissionOrderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
