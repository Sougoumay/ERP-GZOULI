package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Lister les tâches d'un projet
    List<Task> findByProjectId(Long projectId);

    // Pour les KPIs (Calcul du %)
    long countByProjectId(Long projectId);
    long countByProjectIdAndIsCompletedTrue(Long projectId);
}
