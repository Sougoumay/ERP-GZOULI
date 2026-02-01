package com.gzouli.ERP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService{
    private final S3Client s3Client;


    @Value("${aws.s3.bucketName}") // Ajoutez ceci dans application.properties
    private String bucketName;
    private final S3Presigner s3Presigner;

    public FileStorageServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // Générer un nom unique pour éviter les conflits (ex: uuid_facture.pdf)
        String key = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key; // On retourne la clé pour la stocker en BDD
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload S3", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    @Override
    public String generatePresignedUrl(String key) {
        if (key == null) return null;
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // Lien valide 15 min
                .getObjectRequest(objectRequest).build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

//    @PostConstruct
//    public void checkBucketExistence() {
//        try {
//            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
//                    .bucket(bucketName)
//                    .build();
//
//            // Cette commande légère vérifie si le bucket existe et si on a les droits
//            s3Client.headBucket(headBucketRequest);
//
//            System.out.println("✅ Connexion S3 réussie. Bucket connecté : " + bucketName);
//
//        } catch (Exception e) {
//            System.err.println("❌ ERREUR CRITIQUE S3 : Le bucket '" + bucketName + "' est introuvable ou inaccessible !");
//            // On arrête l'appli car sans stockage, elle ne sert à rien
//            throw new RuntimeException("Impossible de démarrer : Vérifiez la config S3");
//        }
//    }
}
