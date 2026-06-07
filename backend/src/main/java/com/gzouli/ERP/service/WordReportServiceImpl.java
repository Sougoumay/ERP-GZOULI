package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.ProjectRepository;
import com.gzouli.ERP.dao.SiteJournalRepository;
import com.gzouli.ERP.dto.ReportRequestDTO;
import com.gzouli.ERP.entity.JournalPhoto;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.SiteJournal;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WordReportServiceImpl implements WordReportService{

    private final ProjectRepository projectRepository;
    private final SiteJournalRepository siteJournalRepository;
    private final FileStorageService s3Service; // Votre service pour télécharger depuis AWS

    public WordReportServiceImpl(ProjectRepository projectRepository,
                             SiteJournalRepository siteJournalRepository,
                                 FileStorageService s3Service) {
        this.projectRepository = projectRepository;
        this.siteJournalRepository = siteJournalRepository;
        this.s3Service = s3Service;
    }

    @Override
    public byte[] generateWordReport(Long projectId, ReportRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        // 1. Récupérer tous les journaux dans la plage de dates
        List<SiteJournal> journals = siteJournalRepository.findByProjectIdAndWorkDateBetweenOrderByWorkDateAsc(
                projectId, dto.getStartDate(), dto.getEndDate());

        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // --- A. TITRE DU RAPPORT ---
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("RAPPORT MENSUEL DE CHANTIER");
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.addBreak();

            XWPFRun subtitleRun = title.createRun();
            subtitleRun.setText("Projet : " + project.getName());
            subtitleRun.setFontSize(14);
            subtitleRun.addBreak();

            // --- B. INTRODUCTION (Venant de l'éditeur Quill) ---
            XWPFParagraph introTitle = document.createParagraph();
            XWPFRun introTitleRun = introTitle.createRun();
            introTitleRun.setText("1. Introduction");
            introTitleRun.setBold(true);
            introTitleRun.setFontSize(16);

            XWPFParagraph introContent = document.createParagraph();
            XWPFRun introRun = introContent.createRun();
            // Note: Pour un MVP, on insère le HTML en brut ou nettoyé de ses balises.
            // La conversion parfaite HTML -> Word nécessite une librairie additionnelle lourde.
            String cleanText = dto.getIntroductionHtml().replaceAll("<[^>]*>", ""); // Enlève les balises HTML basiques
            introRun.setText(cleanText);
            introRun.addBreak();

            // --- C. TABLEAU : JOURNAL D'ACTIVITÉ ---
            XWPFParagraph tableTitle = document.createParagraph();
            tableTitle.createRun().setText("2. Journal d'activité de l'équipe");
            tableTitle.createRun().setBold(true);

            XWPFTable table = document.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Date");
            headerRow.addNewTableCell().setText("Lieu");
            headerRow.addNewTableCell().setText("Activités");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (SiteJournal journal : journals) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(journal.getWorkDate().format(formatter));
                row.getCell(1).setText(journal.getLocation() != null ? journal.getLocation() : "");
                row.getCell(2).setText(journal.getTaskDescription());
            }

            // --- D. ILLUSTRATIONS PHOTOGRAPHIQUES ---
            document.createParagraph().createRun().addBreak(BreakType.PAGE); // Saut de page

            XWPFParagraph photoTitle = document.createParagraph();
            XWPFRun photoTitleRun = photoTitle.createRun();
            photoTitleRun.setText("3. Illustrations Photographiques");
            photoTitleRun.setBold(true);
            photoTitleRun.setFontSize(16);

            for (SiteJournal journal : journals) {
                if (journal.getPhotos() != null && !journal.getPhotos().isEmpty()) {
                    for (JournalPhoto photo : journal.getPhotos()) {

                        // 1. Téléchargement physique de l'image depuis AWS S3
                        byte[] imageBytes = s3Service.downloadFileBytes(photo.getFileKey());

                        // 2. Insertion de l'image dans le Word
                        XWPFParagraph picParagraph = document.createParagraph();
                        picParagraph.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun picRun = picParagraph.createRun();

                        try (ByteArrayInputStream is = new ByteArrayInputStream(imageBytes)) {
                            // Dimensions ajustables (ici 400x300)
                            picRun.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, "photo.jpg",
                                    Units.toEMU(400), Units.toEMU(300));
                        }

                        // 3. Ajout de la légende (Commentaire technique) sous la photo
                        XWPFParagraph captionPara = document.createParagraph();
                        captionPara.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun captionRun = captionPara.createRun();
                        captionRun.setItalic(true);
                        captionRun.setText(photo.getDescription());
                        captionRun.addBreak();
                    }
                }
            }

            // Écriture finale dans le flux mémoire
            document.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du document Word", e);
        }
    }
}
