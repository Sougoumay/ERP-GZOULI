package com.gzouli.ERP.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReportRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String introductionHtml; // Le texte enrichi provenant de Quill
}
