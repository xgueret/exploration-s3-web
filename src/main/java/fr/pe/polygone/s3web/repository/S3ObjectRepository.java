package fr.pe.polygone.s3web.repository;

import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * repository de manipulation des objets bucket s3
 */
public interface S3ObjectRepository {

    /**
     * lister les buckets
     * @return List<String>
     */
    List<String> listBucket();

    /**
     * lister tous les objets présent dans un Compartiment (ou Bucket)
     * @return
     */
    List<S3Object> listObjects();

    /**
     * charger un fichier dans un compartiment (Bucket) s3
     * @param file
     * @param key
     * @return
     * @throws IOException
     */
    PutObjectResponse upload(File file, String key) throws IOException;

    /**
     * supprimer un fichier dans un bucket s3
     * @param key : clef de l'objet s3 à supprimer
     * @return DeleteObjectResponse
     */
    DeleteObjectResponse delete(String key);

    /**
     * télécharger un fichier present dans un compartiment (Bucket) dans les resources de l'apli
     * @param key
     * @param destination
     * @return
     */
    GetObjectResponse download(final String key, final Path destination);

    /**
     * setter bucketName
     * @param bucketName
     */
    void setBucketName(String bucketName);

    /**
     * lister toutes les versions d'un objet
     * @return List<String>
     */
    ListObjectVersionsResponse listObjectVersion();

    /**
     * activer le versionning sur le bucket
     * @return
     */
    PutBucketVersioningResponse enableBucketVersioning();

    /**
     *
     * @return
     */
    GetBucketVersioningResponse getBucketVersioningStatus();

}