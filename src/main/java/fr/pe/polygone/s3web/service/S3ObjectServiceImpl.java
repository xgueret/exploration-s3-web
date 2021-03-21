package fr.pe.polygone.s3web.service;

import fr.pe.polygone.s3web.repository.S3ObjectRepository;
import fr.pe.polygone.s3web.repository.S3ObjectRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class S3ObjectServiceImpl implements S3ObjectService  {


    private S3ObjectRepository s3ObjectRepository;


    @Autowired
    public S3ObjectServiceImpl(S3ObjectRepositoryImpl s3ObjectRepository) {
        this.s3ObjectRepository = s3ObjectRepository;
    }


    @Override
    public List<String> listBucket() {
        return s3ObjectRepository.listBucket();
    }

    @Override
    public List<HashMap<String, Object>> listFile() {

        return this.s3ObjectRepository.listObjects().stream().map(x -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ETag", x.eTag());
            map.put("Size", x.size());
            map.put("Key", x.key());
            map.put("LastModified", x.lastModified());
            map.put("StorageClass", x.storageClassAsString());

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public PutObjectResponse upload(File file, String key) throws IOException {
        return s3ObjectRepository.upload(file,key);
    }


    @Override
    public DeleteObjectResponse delete(String key) {
        return s3ObjectRepository.delete(key);
    }

    @Override
    public GetObjectResponse download(String key, Path destination) throws IOException {

        if (Files.exists(destination)) {
            log.info("Le fichier existe déjà {}", destination);
            Files.delete(destination);
            log.info("Suppression du fichier {}", destination);
        }

        //creation des dossiers parent s'ils n'existent pas
        Path pathParent = destination.getParent();
        Files.createDirectories(Paths.get(pathParent.toString()));

        GetObjectResponse getObjectResponse = s3ObjectRepository.download(key, destination);

        return getObjectResponse;

    }

    @Override
    public  List<HashMap<String, Object>> listObjectVersion(String key){

        List<ObjectVersion> objectVersionList = s3ObjectRepository.listObjectVersion().versions();

        if (key != null) {
            objectVersionList = objectVersionList.stream()
                    .filter(f -> f.key().equals(key))
                    .collect(Collectors.toList());
        }

        return objectVersionList.stream().map(x -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ETag", x.eTag());
            map.put("Size", x.size());
            map.put("Key", x.key());
            map.put("VersionId", x.versionId());
            map.put("IsLatest", x.isLatest());
            map.put("LastModified", x.lastModified());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public PutBucketVersioningResponse enableBucketVersioning() {
        return this.s3ObjectRepository.enableBucketVersioning();
    }

    @Override
    public GetBucketVersioningResponse getBucketVersioningStatus() {
        return this.s3ObjectRepository.getBucketVersioningStatus();
    }
}

