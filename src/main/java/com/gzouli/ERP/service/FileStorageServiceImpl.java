package com.gzouli.ERP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

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
    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }


    @Override
    public void confirmFile(String key) {
        if (key == null || key.isEmpty()) return;

        // 1. Création du Tag
        Tag tag = Tag.builder()
                .key("status")
                .value("confirmed")
                .build();

        Tagging tagging = Tagging.builder()
                .tagSet(tag)
                .build();

        // 2. Préparation de la requête de Tagging
        PutObjectTaggingRequest taggingRequest = PutObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(key)
                .tagging(tagging)
                .build();

        // 3. Envoi de la requête à AWS
        s3Client.putObjectTagging(taggingRequest);
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

    /**
     * 2. POUR L'ÉCRITURE (PUT) - Utilisé par Angular pour uploader un fichier
     */
    @Override
    public String generateUploadUrl(String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Télécharge un fichier depuis AWS S3 et le retourne sous forme de tableau d'octets (byte[]).
     * Parfait pour insérer des images dans un document Word ou PDF en mémoire.
     */
    public byte[] downloadFileBytes(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName) // Assurez-vous d'avoir votre variable contenant le nom du bucket ici
                    .key(fileKey)
                    .build();

            // Utilisation du ResponseTransformer pour convertir directement le flux S3 en byte[]
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(
                    getObjectRequest,
                    ResponseTransformer.toBytes()
            );

            return objectBytes.asByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du téléchargement de l'image depuis S3 (Clé: " + fileKey + ")", e);
        }
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
