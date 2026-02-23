package com.gzouli.ERP.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsDTO {
    // 1. Vue Macro (Potentiel)
    private Double totalMarketValueHT; // Somme totale des marchés signés (Carnet de commande)

    // 2. Vue Réelle (Cash)
    private Double certifiedTurnover;  // Somme des factures VALIDÉES (C'est le vrai CA actuel)
    private Double pendingTurnover;    // Somme des factures DÉPOSÉES mais NON validées

    // 3. Vue Sorties
    private Double totalExpenses;      // Somme Salaires + Matériel + Divers

    // 4. Résultats
    private Double realNetMargin;      // (CA Certifié - Dépenses) -> La vraie rentabilité à date
    private Double globalCompletionRate; // Ratio : (CA Certifié / Total Marchés) * 100

    // ... (Reste de la météo du jour inchangé)
    private List<DailyReportStatus> dailyReports;

    @Data
    public static class DailyReportStatus {
        private String employeeName;
        private String role;
        private String projectName;
        private boolean reportSubmitted;
        private String submissionTime;
    }
}
