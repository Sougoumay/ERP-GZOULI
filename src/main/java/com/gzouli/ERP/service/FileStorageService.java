package com.gzouli.ERP.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String path);

    void confirmFile(String key);

    void deleteFile(String key);

    String generatePresignedUrl(String key);

    String generateUploadUrl(String key, String contentType);
}
