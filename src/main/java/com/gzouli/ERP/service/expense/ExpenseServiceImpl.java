package com.gzouli.ERP.service.expense;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dao.ExpenseRepository;
import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dto.expense.ExpenseDTO;
import com.gzouli.ERP.dto.expense.ExpenseRegistrationDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Expense;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.exception.ResourceNotFoundException; // Votre exception perso
import com.gzouli.ERP.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Génère le constructeur pour les injections
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void addExpense(Long projectId, ExpenseRegistrationDTO dto) {

        // 1. Récupération des entités liées
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        // 2. Création de la dépense
        Expense expense = new Expense();
        expense.setProject(project);
        expense.setPerformedBy(employee); // Qui a payé ?

        expense.setType(dto.getType());
        expense.setLabel(dto.getLabel());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());

        // Lien S3
        expense.setS3Key(dto.getFileKey());
        expense.setFileName(dto.getFileName());

        expenseRepository.save(expense);
    }

    @Override
    public List<ExpenseDTO> getExpensesByProject(Long projectId) {
        return expenseRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense introuvable"));

        String s3Key = expense.getS3Key();

        // 1. Suppression en BDD D'ABORD
        expenseRepository.delete(expense);

        // IMPORTANT : Forcer Hibernate à exécuter le SQL tout de suite.
        // Si une contrainte (Foreign Key) empêche la suppression, l'exception éclate ICI.
        // Et comme on n'a pas encore touché à S3, le fichier est sauf.
        expenseRepository.flush();

        // 2. Si on arrive ici, c'est que la BDD est OK. On supprime le fichier S3.
        if (s3Key != null) {
            try {
                fileStorageService.deleteFile(s3Key);
            } catch (Exception e) {
                // Cas rare : La BDD est supprimée, mais S3 échoue (coupure réseau).
                // Ce n'est pas grave pour l'utilisateur, juste un fichier orphelin qui traîne.
                // On log l'erreur pour un nettoyage futur, mais on ne bloque pas.
                System.err.println("Attention : Fichier S3 orphelin non supprimé : " + s3Key);
            }
        }
    }

    // Mapper utilitaire pour garder le code propre
    private ExpenseDTO mapToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setType(expense.getType());
        dto.setLabel(expense.getLabel());
        dto.setAmount(expense.getAmount());
        dto.setFileName(expense.getFileName());

        // Génération URL signée (valide 15 min)
        dto.setDownloadUrl(fileStorageService.generatePresignedUrl(expense.getS3Key()));

        // Info Employé aplatie
        if (expense.getPerformedBy() != null) {
            dto.setPerformedById(expense.getPerformedBy().getId());
            dto.setPerformedByName(expense.getPerformedBy().getFirstName() + " " + expense.getPerformedBy().getLastName());
        }
        return dto;
    }
}
