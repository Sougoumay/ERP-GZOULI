package com.gzouli.ERP.dto.project;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SiteJournalSummaryDTO {
    private Long id;
    private LocalDate workDate;
    private String location;
    private String authorName; // Concaténation Nom + Prénom
}
