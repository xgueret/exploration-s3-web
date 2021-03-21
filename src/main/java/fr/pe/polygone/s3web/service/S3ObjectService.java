package fr.pe.polygone.s3web.service;

import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * service de manipulation des objets bucket s3
 */
public interface S3ObjectService {
    /**
     * lister les buckets
     * @return List<String>
     */
    List<String> listBucket();

    /**
     * lister tous les fichiers d'un bucket
     * @return
     */
    List<HashMap<String, Object>> listFile();

    /**
     * charger un fichier dans un bucket s3
     * @param file
     * @param key
     * @return
     * @throws IOException
     */
    PutObjectResponse upload(File file, String key) throws IOException;


    /**
     * supprimer un fichier dans un bucket s3
     * @param key : clef de l'objet s3
     * @return DeleteObjectResponse
     */
    DeleteObjectResponse delete(String key);

    /**
     * télécharger un fichier present dans un bucket dans les resources de l'apli
     * @param fileName
     * @param destination
     * @return
     */
    GetObjectResponse download(final String fileName, final Path destination) throws IOException;

    /**
     * lister toutes les versions d'un object
     * @return List<ObjectVersion>
     */
    List<HashMap<String, Object>> listObjectVersion(String key);

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


