package com.gzouli.ERP.dto.project;

import lombok.Data;

@Data
public class JournalPhotoResponseDTO {
    private String fileUrl; // Le vrai lien S3 généré
    private String description;
}
