package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ExpenseRepository;
import com.gzouli.ERP.dao.InvoiceRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.DashboardStatsDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Expense;
import com.gzouli.ERP.entity.Invoice;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
//@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final ExpenseRepository expenseRepository;
    private final EmployeeRepository employeeRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardServiceImpl(ProjectRepository projectRepository, ExpenseRepository expenseRepository, EmployeeRepository employeeRepository, InvoiceRepository invoiceRepository) {
        this.projectRepository = projectRepository;
        this.expenseRepository = expenseRepository;
        this.employeeRepository = employeeRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // =================================================================
        // 1. KPIs FINANCIERS (DONNÉES RÉELLES)
        // =================================================================
        List<Project> projects = projectRepository.findAll();
        List<Expense> expenses = expenseRepository.findAll();
        List<Invoice> invoices = invoiceRepository.findAll();

        // 1. Carnet de Commandes (Total des Marchés Signés)
        double totalMarketValue = projects.stream()
                .mapToDouble(p -> p.getAmountExTax() != null ? p.getAmountExTax() : 0)
                .sum();

        // 2. CA Certifié (Factures validées = TRUE)
        double certifiedRevenue = invoices.stream()
                .filter(Invoice::getIsCertified)
                .mapToDouble(Invoice::getAmount)
                .sum();

        // 3. CA En Attente (Factures validées = FALSE)
        double pendingRevenue = invoices.stream()
                .filter(inv -> !inv.getIsCertified())
                .mapToDouble(Invoice::getAmount)
                .sum();

        // 4. Total des Dépenses
        double totalExp = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        // --- Remplissage DTO ---
        stats.setTotalMarketValueHT(totalMarketValue);
        stats.setCertifiedTurnover(certifiedRevenue);
        stats.setPendingTurnover(pendingRevenue);
        stats.setTotalExpenses(totalExp);

        // 5. Calculs KPI
        // Marge Réelle = Ce qu'on a vraiment gagné (certifié) - Ce qu'on a dépensé
        stats.setRealNetMargin(certifiedRevenue - totalExp);

        // Taux d'avancement financier global de l'entreprise
        if (totalMarketValue > 0) {
            double ratio = (certifiedRevenue / totalMarketValue) * 100;
            stats.setGlobalCompletionRate(Math.round(ratio * 100.0) / 100.0); // Arrondi 2 décimales
        } else {
            stats.setGlobalCompletionRate(0.0);
        }

        // =================================================================
        // 2. MÉTÉO DU PERSONNEL (SIMULATION POUR DÉMO CLIENT)
        // =================================================================
        // TODO: Une fois l'App Mobile prête, remplacer cette partie par une requête
        // vers la table 'SiteJournal' (ex: siteJournalRepository.findByDate(LocalDate.now()))

        List<Employee> employees = employeeRepository.findByRoleNot(Role.ADMIN);
        List<DashboardStatsDTO.DailyReportStatus> reports = getDailyReportStatuses(employees, projects);

        stats.setDailyReports(reports);

        return stats;
    }

    private static List<DashboardStatsDTO.DailyReportStatus> getDailyReportStatuses(List<Employee> employees, List<Project> projects) {
        List<DashboardStatsDTO.DailyReportStatus> reports = new ArrayList<>();
        Random rand = new Random();

        for (Employee emp : employees) {
            // On ne garde que les actifs
            if (!emp.isActive()) continue;

            DashboardStatsDTO.DailyReportStatus status = new DashboardStatsDTO.DailyReportStatus();
            status.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
            status.setRole(emp.getRole().name());

            // Assignation Projet (Fictive pour la démo si pas d'historique précis aujourd'hui)
            if (!projects.isEmpty()) {
                status.setProjectName(projects.get(rand.nextInt(projects.size())).getName());
            } else {
                status.setProjectName("Non affecté");
            }

            // SIMULATION INTELLIGENTE (70% de chance d'avoir fait le rapport)
            boolean submitted = rand.nextDouble() > 0.3;
            status.setReportSubmitted(submitted);

            if (submitted) {
                // Heure aléatoire crédible (fin de journée)
                int hour = 16 + rand.nextInt(4); // entre 16h et 19h
                int min = rand.nextInt(60);
                status.setSubmissionTime(String.format("%02d:%02d", hour, min));
            }

            reports.add(status);
        }
        return reports;
    }
}