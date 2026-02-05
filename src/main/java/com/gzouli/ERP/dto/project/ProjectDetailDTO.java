package com.gzouli.ERP.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectDetailDTO extends ProjectSummaryDTO {

//    // Calculés à la volée (Logique Audio [11])
//    private Double consumedBudgetPercentage;
//    private Integer monthsElapsed;

    private List<TeamMemberDTO> teamMembers;

    // Total des recettes théoriques (Somme de toutes les factures)
    private Double totalInvoicesSubmitted;
    private Double totalInvoicesCertified; // Total Recettes Validées
    private Double totalExpenses;          // Total Dépenses
    private Double currentMargin;          // Gain (Recettes - Dépenses)
}
