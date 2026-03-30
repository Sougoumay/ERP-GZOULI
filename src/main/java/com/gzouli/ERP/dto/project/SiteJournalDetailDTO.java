package com.gzouli.ERP.dto.project;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SiteJournalDetailDTO {
    private Long id;
    private LocalDate workDate;
    private String location;
    private String serviceType;
    private String authorName;
    private String taskDescription;

    // Les URLs générées prêtes à être affichées dans vos sliders/slices Angular
    private List<String> pvFileUrls;
    private List<JournalPhotoResponseDTO> photos;
}
