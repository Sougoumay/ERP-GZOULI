package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.ProjectMissionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMissionOrderRepository extends JpaRepository<ProjectMissionOrder, Long> {

    List<ProjectMissionOrder> findProjectMissionByIdOrderByEffectiveDateAsc(Long id);

    List<ProjectMissionOrder> findProjectMissionByProjectIdOrderByEffectiveDate(Long projectId);
}
