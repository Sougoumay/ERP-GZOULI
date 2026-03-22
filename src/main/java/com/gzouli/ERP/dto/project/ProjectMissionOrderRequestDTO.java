package com.gzouli.ERP.dto.project;

import com.gzouli.ERP.enums.MissionOrderType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectMissionOrderRequestDTO {
    private MissionOrderType type;           // DEMARRAGE, ARRET, REPRISE
    private LocalDate effectiveDate;  // Date d'effet
    private String fileKey;
}
