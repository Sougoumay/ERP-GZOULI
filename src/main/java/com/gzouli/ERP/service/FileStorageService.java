package com.gzouli.ERP.service;

public interface FileStorageService {

    void confirmFile(String key);

    void deleteFile(String key);

    String generatePresignedUrl(String key);

    String generateUploadUrl(String key, String contentType);
}
