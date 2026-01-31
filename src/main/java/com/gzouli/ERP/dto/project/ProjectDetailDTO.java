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

    // La liste des membres de l'équipe pour l'affichage
    private List<TeamMemberDTO> teamMembers;
}
