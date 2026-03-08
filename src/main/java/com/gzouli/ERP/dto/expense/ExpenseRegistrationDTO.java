package com.gzouli.ERP.dto.expense;

import com.gzouli.ERP.enums.ExpenseType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseRegistrationDTO {
    private ExpenseType type;       // GASOIL, MAINTENANCE, FOURNITURE...
    private String label;           // Description (ex: "Vidange Ford")
    private Double amount;          // Montant TTC
    private LocalDate expenseDate;  // Date de la dépense
    private String fileKey;
    private String fileName;
    private Long employeeId;        // ID de l'employé qui a effectué la dépense
}
