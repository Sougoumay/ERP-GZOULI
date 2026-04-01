package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.ReportRequestDTO;

public interface WordReportService {
    byte[] generateWordReport(Long projectId, ReportRequestDTO dto);
}
