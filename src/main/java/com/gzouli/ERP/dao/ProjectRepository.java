package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Pour éviter de créer deux projets avec le même nom de marché
    boolean existsByName(String name);

    // Pour n'afficher que les projets actifs dans certaines listes
    List<Project> findByActiveTrue();
}
