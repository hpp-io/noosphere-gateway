package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.security.SecurityUtils;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreRequest;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreValidationRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keystore")
public class KeystoreResource {

    private final Logger log = LoggerFactory.getLogger(KeystoreResource.class);

    @PostMapping("/create")
    public ResponseEntity<Resource> createKeystore(@RequestBody KeystoreRequest request) {
        try {
            Path tempFile = Files.createTempFile("keystore", ".p12");
            SecurityUtils.createKeyStore(tempFile, request.getKeyAlias(), request.getPassword(), request.getPrivateKey());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(tempFile));
            Files.delete(tempFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getKeyAlias() + ".p12");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (IOException e) {
            log.error("Failed to create keystore", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateKeystore(@RequestBody KeystoreValidationRequest request) {
        try {
            byte[] decodedFile = Base64.getDecoder().decode(request.getFileContent());
            try (InputStream is = new ByteArrayInputStream(decodedFile)) {
                String value = SecurityUtils.validateKeyStore(is, request.getKeyAlias(), request.getPassword());
                if (value != null) {
                    return ResponseEntity.ok(value);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
        } catch (Exception e) {
            log.error("Failed to validate keystore", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
