package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Pour éviter de créer deux projets avec le même nom de marché
    boolean existsByName(String name);

    // Pour n'afficher que les projets actifs dans certaines listes
    List<Project> findByActiveTrue();

    List<Project> findBySupervisorsId(Long employeeId);

    // Cette requête récupère le projet SI :
    // - L'employé est dans la liste des superviseurs
    // - OU SI l'employé est assigné à au moins une tâche de ce projet
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN p.supervisors s " +
            "LEFT JOIN p.tasks t " +
            "WHERE s.id = :employeeId OR t.assignee.id = :employeeId")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);
}
