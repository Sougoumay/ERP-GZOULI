package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.project.*;

import java.util.List;

public interface ProjectService {

    // --- CRUD DE BASE ---
    ProjectSummaryDTO createProject(ProjectRegistrationDTO dto);
    ProjectSummaryDTO updateProject(Long id, ProjectSummaryDTO dto);
    ProjectDetailDTO getProjectById(Long id);
    List<ProjectSummaryDTO> getAllProjects(); // Retourne tout (Actif + Inactif)

    /**
     * Désactive (Soft Delete) ou Réactive un projet.
     * Un projet désactivé n'apparaît plus dans les sélecteurs de saisie journalière.
     */
    void toggleProjectStatus(Long id, boolean isActive);

    // --- FONCTIONNALITÉS AVANCÉES (A implémenter plus tard) ---

    // Gestion de l'équipe (Audio [30])
    void assignSupervisor(Long projectId, Long employeeId);
    void removeSupervisor(Long projectId, Long employeeId);

    // Finances & Rentabilité (Audio [18-20])
    // Calcule : Montant HT - (Salaires + Charges Fixes + Charges Variables)
    Double calculateProjectProfitability(Long projectId);

    void addInvoice(Long pId, InvoiceDTO iDto);

    // Reporting (Audio [49])
    // Prépare les données pour le PDF mensuel
    MonthlyReportDataDTO getMonthlyReportData(Long projectId, int month, int year);
}
