package com.gzouli.ERP.service.project;

import com.gzouli.ERP.dto.project.SiteJournalDetailDTO;
import com.gzouli.ERP.dto.project.SiteJournalRequestDTO;
import com.gzouli.ERP.dto.project.SiteJournalSummaryDTO;
import com.gzouli.ERP.entity.SiteJournal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SiteJournalService {
    void createJournal(Long projectId, SiteJournalRequestDTO dto);

    List<SiteJournalSummaryDTO> getJournalsByProject(Long projectId);

    SiteJournalDetailDTO getJournalDetails(Long journalId);
}
