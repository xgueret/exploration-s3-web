package fr.pe.polygone.s3web.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class S3ObjectRepositoryImpl implements S3ObjectRepository {

    private S3Client s3Client;
    private String bucketName;

    @Autowired
    public S3ObjectRepositoryImpl(S3Client s3Client,  @Value("${s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public List<String> listBucket() {
        log.info("lister tous les buckets s3");
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        List<String> list = new ArrayList<>();

        log.info("Buckets:");
        for (Bucket b : listBucketsResponse.buckets()) {
            log.info("* {}", b);
            list.add(b.toString());
        }

        return list;
    }

    @Override
    public List<S3Object> listObjects() {
        log.info("lister tous les objets présent dans le bucket s3");

        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

        ListObjectsResponse res = s3Client.listObjects(listObjects);

        return res.contents();

    }


    @Override
    public PutObjectResponse upload(File file, String key) throws IOException {

        log.info("pousser le fichier {} dans le bucket s3 {} associé à la clef {}", file.getName(), bucketName, key);

        return s3Client.putObject(
                PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                file.toPath());
    }

    @Override
    public DeleteObjectResponse delete(String key) {

        log.info("supprimer le fichier présent dans le bucket s3");
        return s3Client.deleteObject(
                DeleteObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
    }

    @Override
    public GetObjectResponse download(String key, Path destination) {

        log.info("Télécharger le fichier associé à la clef {} " +
                "à partir du bucket s3 {} vers la destination {}", key, bucketName, destination);

        final GetObjectRequest request = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(request, destination);

    }

    @Override
    public ListObjectVersionsResponse listObjectVersion() {
        log.info("lister toutes les versions des objects");
        ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder()
                .bucket(bucketName)
                .build();

        return s3Client.listObjectVersions(listObjectVersionsRequest);

    }

    @Override
    public PutBucketVersioningResponse enableBucketVersioning() {

        log.info("On active le versionning sur le Bucket suivant : {}", bucketName);
        return s3Client.putBucketVersioning(PutBucketVersioningRequest.builder()
                .bucket(bucketName)
                .versioningConfiguration(
                        VersioningConfiguration.builder()
                                .status(BucketVersioningStatus.ENABLED)
                                .build())
                .build());

    }


    @Override
    public void setBucketName(String bucketName) {

    }

    @Override
    public GetBucketVersioningResponse getBucketVersioningStatus() {
        GetBucketVersioningRequest getBucketVersioningRequest = GetBucketVersioningRequest.builder()
                .bucket(bucketName)
                .build();
        return s3Client.getBucketVersioning(getBucketVersioningRequest);

    }

}