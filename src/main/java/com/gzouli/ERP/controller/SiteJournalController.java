package com.gzouli.ERP.controller;


import com.gzouli.ERP.dto.project.SiteJournalDetailDTO;
import com.gzouli.ERP.dto.project.SiteJournalRequestDTO;
import com.gzouli.ERP.dto.project.SiteJournalSummaryDTO;
import com.gzouli.ERP.service.project.SiteJournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/journals")
public class SiteJournalController {

    private final SiteJournalService siteJournalService;

    public SiteJournalController(SiteJournalService siteJournalService) {
        this.siteJournalService = siteJournalService;
    }

    @PostMapping
    public ResponseEntity<?> createJournal(@PathVariable Long projectId,
                                           @RequestBody SiteJournalRequestDTO dto) {

        siteJournalService.createJournal(projectId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SiteJournalSummaryDTO>> getProjectJournals(@PathVariable Long projectId) {
        List<SiteJournalSummaryDTO> journals = siteJournalService.getJournalsByProject(projectId);
        return ResponseEntity.ok(journals);
    }

    @GetMapping("/{journalId}")
    public ResponseEntity<SiteJournalDetailDTO> getJournalDetails(
            @PathVariable("projectId") Long projectId,
            @PathVariable("journalId") Long journalId) {

        // Le frontend appellera cette route en cliquant sur le bouton "Oeil"
        SiteJournalDetailDTO details = siteJournalService.getJournalDetails(journalId);
        return ResponseEntity.ok(details);
    }
}
