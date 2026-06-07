package com.gzouli.ERP.controller;

import com.gzouli.ERP.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final FileStorageService s3Service;

    public S3Controller(FileStorageService s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String path,
            @RequestParam String fileName,
            @RequestParam String contentType) {

        // 1. On génère un chemin unique pour éviter que deux fichiers s'écrasent
        String key = path + "/"
                + LocalDate.now().getYear() + "/"
                + LocalDate.now().getMonth() + "/"
                + UUID.randomUUID() + "_" + fileName.replaceAll("\\s+", "_");

        // 2. On demande au service de créer le lien
        String presignedUrl = s3Service.generateUploadUrl(key, contentType);

        // 3. On renvoie le lien ET la clé (le Front aura besoin de la clé pour l'étape finale)
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("fileKey", key);

        return ResponseEntity.ok(response);
    }
}
