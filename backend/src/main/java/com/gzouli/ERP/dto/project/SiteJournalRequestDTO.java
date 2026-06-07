package com.gzouli.ERP.dto.project;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SiteJournalRequestDTO {
    private LocalDate workDate;
    private String location;
    private String taskDescription;
    private String serviceType;

    // Rempli uniquement si l'Admin fait la saisie à la place d'un technicien
    private Long authorId;

    private List<String> pvFileKeys;
    private List<JournalPhotoDTO> photos;
}
