package fr.pe.polygone.s3web.controller;

import fr.pe.polygone.s3web.service.S3ObjectService;
import fr.pe.polygone.s3web.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.isTrue;

@RestController
@RequestMapping("/")
@Slf4j
public class BucketController {

    private S3ObjectService s3ObjectService;

    /**
     *
     * @param S3ObjectService
     */
    BucketController(S3ObjectService S3ObjectService) {
        this.s3ObjectService = S3ObjectService;
    }

    @PostMapping("/putObject")
    public ResponseEntity<String> putObject(@RequestPart(value = "file") MultipartFile multipartFile, @RequestParam String key) throws IOException {

        File file = FileUtils.convertMultiPartToFile(multipartFile);
        PutObjectResponse putObjectResponse = this.s3ObjectService.upload(file, key);

        if (!putObjectResponse.sdkHttpResponse().isSuccessful()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok (putObjectResponse.toString());
    }



    @GetMapping("/getObject")
    public ResponseEntity<String> getObject(@RequestBody Map<String, Object> payload) throws IOException {

        try {

            isTrue(payload.containsKey("key"), "la ref de l'objet est obligatoire");
            isTrue(payload.containsKey("destination"), "un dossier destination est obligatoire");

            Path destination = Paths.get((String) payload.get("destination"));

            GetObjectResponse getObjectResponse = this.s3ObjectService.download((String) payload.get("key"), destination);

            if (getObjectResponse.sdkHttpResponse().isSuccessful()) {
                return ResponseEntity.ok ("successfully download");
            } else {
                return ResponseEntity.badRequest().body("download failed");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

    @DeleteMapping("/deleteObject")
    public  ResponseEntity<String> deleteFile(@RequestPart(value = "key") String key) {
        //supprimer le fichier présent dans le bucket s3
        DeleteObjectResponse deleteObjectResponse = this.s3ObjectService.delete(key);

        if (deleteObjectResponse.sdkHttpResponse().isSuccessful()) {
            return ResponseEntity.ok().body("successfully deleted");
        } else {
            return ResponseEntity.badRequest().body("la suppression de l'objet a échoué");
        }
    }

    @GetMapping("/listObjects")
    public ResponseEntity<List<HashMap<String, Object>>> listObjects() {
        //lister tous les fichiers présent dans le bucket s3
        List<HashMap<String, Object>> retour = this.s3ObjectService.listFile();
        return ResponseEntity.ok().body(retour);
    }

    @GetMapping("/listbucket")
    public List<String> listBucket() {
        //lister tous les buckets s3
        return this.s3ObjectService.listBucket();
    }

    @GetMapping("/listObjectVersion")
    public ResponseEntity<List<HashMap<String, Object>>> listObjectVersion(@RequestPart(value = "key",
            required = false) String key) {
        List<HashMap<String, Object>> retour = this.s3ObjectService.listObjectVersion(key);
        return ResponseEntity.ok (retour);
    }

    @GetMapping("/enableBucketVersioning")
    public PutBucketVersioningResponse enableBucketVersioning() {
        return this.s3ObjectService.enableBucketVersioning();
    }

    @GetMapping("/bucketVersioningStatus")
    public ResponseEntity<HashMap<String, Object>> getBucketVersioningStatus() {
        GetBucketVersioningResponse getBucketVersioningResponse = this.s3ObjectService.getBucketVersioningStatus();

        if (!getBucketVersioningResponse.sdkHttpResponse().isSuccessful()) {
            return ResponseEntity.badRequest().build();
        }

        if (getBucketVersioningResponse.status() == null) {
            return ResponseEntity.noContent().build();
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", getBucketVersioningResponse.status().toString());
        return ResponseEntity.ok (map);

    }
}
