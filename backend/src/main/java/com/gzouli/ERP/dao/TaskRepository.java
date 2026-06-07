package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Lister les tâches d'un projet
    List<Task> findByProjectId(Long projectId);

    // Pour les KPIs (Calcul du %)
    long countByProjectId(Long projectId);
    long countByProjectIdAndIsCompletedTrue(Long projectId);

    // Somme de tous les poids des tâches du projet
    @Query("SELECT COALESCE(SUM(t.weight), 0.0) FROM Task t WHERE t.project.id = :projectId")
    double sumWeightsByProjectId(@Param("projectId") Long projectId);

    // Somme des poids des tâches terminées
    @Query("SELECT COALESCE(SUM(t.weight), 0.0) FROM Task t WHERE t.project.id = :projectId AND t.isCompleted = true")
    double sumCompletedWeightsByProjectId(@Param("projectId") Long projectId);
}
