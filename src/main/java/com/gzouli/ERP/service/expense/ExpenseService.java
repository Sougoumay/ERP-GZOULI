package com.gzouli.ERP.service.expense;

import com.gzouli.ERP.dto.expense.ExpenseDTO;
import com.gzouli.ERP.dto.expense.ExpenseRegistrationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExpenseService {
    // Ajout avec fichier justificatif
    void addExpense(Long projectId, ExpenseRegistrationDTO dto, MultipartFile file);

    // Liste pour un projet spécifique
    List<ExpenseDTO> getExpensesByProject(Long projectId);

    // Suppression (Nettoyage BDD + S3)
    void deleteExpense(Long expenseId);
}
