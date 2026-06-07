package com.gzouli.ERP.dto.expense;

import com.gzouli.ERP.enums.ExpenseType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseDTO {
    private Long id;
    private LocalDate expenseDate;
    private ExpenseType type;
    private String label;
    private Double amount;

    // Info Fichier S3
    private String fileName;
    private String downloadUrl; // URL signée temporaire

    // Info Employé (Pour affichage direct dans le tableau)
    private Long performedById;
    private String performedByName; // Ex: "Hamid ANDJA"

    // Le lien optionnel
    private Long linkedCarId;       // Pour l'envoi (Front -> Back)
    private String linkedCarMatricule; // Pour l'affichage (Back -> Front)
}
