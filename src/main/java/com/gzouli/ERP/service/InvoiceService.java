package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.invoice.InvoiceDTO;
import com.gzouli.ERP.dto.invoice.InvoiceRegistrationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InvoiceService {

    void addInvoiceToProject(Long projectId, InvoiceRegistrationDTO dto, MultipartFile file);

    List<InvoiceDTO> getInvoicesByProject(Long projectId);

    void updateInvoice(Long invoiceId, InvoiceRegistrationDTO dto, MultipartFile file);

    void toggleCertification(Long invoiceId);

    void deleteInvoice(Long invoiceId);
}
