package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dto.project.ProjectMissionOrderRequestDTO;
import com.gzouli.ERP.dto.project.ProjectMissionOrderResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MissionOrderService {

    ProjectMissionOrderResponseDTO createOrder(Long projectId, ProjectMissionOrderRequestDTO dto);


    List<ProjectMissionOrderResponseDTO> getOrdersByProject(Long projectId);

    ProjectMissionOrderResponseDTO updateOrder(Long orderId, ProjectMissionOrderRequestDTO dto);

    void deleteOrder(Long orderId);

    long calculateEffectiveDays(Long projectId);
}
