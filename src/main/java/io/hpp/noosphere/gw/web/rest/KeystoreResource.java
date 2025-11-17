package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.security.KeystoreManager;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreRequest;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreValidationRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
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
import org.web3j.crypto.Credentials;

@RestController
@RequestMapping("/api/keystore")
public class KeystoreResource {

  private final Logger log = LoggerFactory.getLogger(KeystoreResource.class);

  @PostMapping("/create")
  public ResponseEntity<Resource> createKeystore(@RequestBody KeystoreRequest request) {
    try {
      Path tempFile = Files.createTempFile("keystore", ".p12");
      if (request.getIsWallet()) {
        KeystoreManager.createKeyStoreWithHexPrivateKey(tempFile, request.getPassword(), request.getKeyAlias(), request.getPrivateKey());
      } else {
        KeystoreManager.createKeyStoreWithUtf8String(tempFile, request.getPassword(), request.getKeyAlias(), request.getPrivateKey());
      }
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
    } catch (IOException | GeneralSecurityException e) {
      log.error("Failed to create keystore", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping("/read")
  public ResponseEntity<String> readKeystore(@RequestBody KeystoreValidationRequest request) {
    try {
      String value = null;
      if (request.getIsWallet()) {
        Credentials credentials = KeystoreManager.readEthKeyFromSecretFromBase64Keystore(request.getFileContent(), request.getPassword(), request.getKeyAlias());
        value = KeystoreManager.readPrivateKeyAsHexString(credentials);
      } else {
        value = KeystoreManager.readSecretKeyAsUtf8StringFromBase64dKeystore(request.getFileContent(), request.getPassword(), request.getKeyAlias());
      }
      if (value != null) {
        return ResponseEntity.ok(value);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      log.error("Failed to validate keystore", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
