package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.ReportRequestDTO;
import com.gzouli.ERP.service.WordReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/projects/{projectId}/generate-report")
public class ReportController {

    private final WordReportService wordReportService;

    public ReportController(WordReportService wordReportService) {
        this.wordReportService = wordReportService;
    }

    @PostMapping
    public ResponseEntity<byte[]> generateReport(@PathVariable Long projectId,
                                                 @RequestBody ReportRequestDTO dto) {

        // Génération du document Word
        byte[] wordDocument = wordReportService.generateWordReport(projectId, dto);

        // Configuration des en-têtes pour forcer le téléchargement du fichier
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", "Rapport_Mensuel.docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(wordDocument);
    }
}
