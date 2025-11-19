package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.KEY_ALIAS_HPP_WALLET_ADDRESS;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.security.KeystoreManager;
import io.hpp.noosphere.gw.web.rest.dto.CreateWalletRequest;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreReadRequest;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/keystore")
public class KeystoreResource {

  private final NoosphereHubClient noosphereHubClient;
  private final Logger log = LoggerFactory.getLogger(KeystoreResource.class);

  public KeystoreResource(ApplicationContext context) {
    this.noosphereHubClient = context.getBean(NoosphereHubClient.class);
  }

  @PostMapping("/create")
  public Mono<ResponseEntity<Resource>> createKeystore(@RequestBody KeystoreRequest request) {
    Mono<Path> pathMono;

    if (Boolean.TRUE.equals(request.getIsWallet()) && Boolean.TRUE.equals(request.getCreateHppWallet())) {
      pathMono =
        Mono
          .fromCallable(() -> {
            KeyStore ks = KeystoreManager.createKeyStore(request.getPassword());
            Credentials credentials = KeystoreManager.addEthWalletV3WithHexPrivateKey(
              ks,
              request.getPassword(),
              request.getKeyAlias(),
              request.getPrivateKey()
            );
            CreateWalletRequest createWalletRequest = new CreateWalletRequest();
            createWalletRequest.setOwnerAddress(credentials.getAddress());
            return new WalletCreationData(ks, createWalletRequest);
          })
          .flatMap(data ->
            noosphereHubClient
              .createWallet(data.createWalletRequest)
              .switchIfEmpty(Mono.error(new IOException("Failed to create HPP wallet in Noosphere Hub.")))
              .flatMap(newHppWallet -> {
                try {
                  Path tempFile = Files.createTempFile("keystore", ".p12");
                  KeystoreManager.addSecretKeyWithUtf8String(
                    data.keyStore,
                    request.getPassword(),
                    KEY_ALIAS_HPP_WALLET_ADDRESS,
                    newHppWallet
                  );
                  KeystoreManager.saveKeyStore(data.keyStore, tempFile, request.getPassword());
                  return Mono.just(tempFile);
                } catch (GeneralSecurityException | IOException e) {
                  return Mono.error(e);
                }
              })
          )
          .subscribeOn(Schedulers.boundedElastic());
    } else {
      pathMono =
        Mono
          .fromCallable(() -> {
            Path tempFile = Files.createTempFile("keystore", ".p12");
            if (Boolean.TRUE.equals(request.getIsWallet())) {
              KeystoreManager.createKeyStoreWithHexPrivateKey(
                tempFile,
                request.getPassword(),
                request.getKeyAlias(),
                request.getPrivateKey()
              );
            } else {
              KeystoreManager.createKeyStoreWithUtf8String(tempFile, request.getPassword(), request.getKeyAlias(), request.getPrivateKey());
            }
            return tempFile;
          })
          .subscribeOn(Schedulers.boundedElastic());
    }

    return pathMono
      .flatMap(path -> {
        try {
          ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
          Files.delete(path); // Clean up the temporary file

          HttpHeaders headers = new HttpHeaders();
          headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getKeyAlias() + ".p12");
          headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
          headers.add(HttpHeaders.PRAGMA, "no-cache");
          headers.add(HttpHeaders.EXPIRES, "0");

          return Mono.just(
            ResponseEntity
              .ok()
              .headers(headers)
              .contentLength(resource.contentLength())
              .contentType(MediaType.APPLICATION_OCTET_STREAM)
              .body((Resource) resource)
          );
        } catch (IOException e) {
          return Mono.error(e);
        }
      })
      .onErrorResume(e -> {
        log.error("Failed to create keystore", e);
        return Mono.just(ResponseEntity.internalServerError().build());
      });
  }

  @PostMapping("/read")
  public ResponseEntity<String> readKeystore(@RequestBody KeystoreReadRequest request) {
    try {
      String value;
      if (request.getIsWallet()) {
        Credentials credentials = KeystoreManager.readEthKeyFromSecretFromBase64Keystore(
          request.getFileContent(),
          request.getPassword(),
          request.getKeyAlias()
        );
        value = KeystoreManager.readPrivateKeyAsHexString(credentials);
      } else if (request.getIsHppWallet()) {
        value = KeystoreManager.readSecretKeyAsUtf8StringFromBase64dKeystore(
          request.getFileContent(),
          request.getPassword(),
          KEY_ALIAS_HPP_WALLET_ADDRESS
        );
      } else {
        value =
          KeystoreManager.readSecretKeyAsUtf8StringFromBase64dKeystore(
            request.getFileContent(),
            request.getPassword(),
            request.getKeyAlias()
          );
      }
      if (value != null) {
        return ResponseEntity.ok(value);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      log.error("Failed to read keystore", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  private static class WalletCreationData {

    final KeyStore keyStore;
    final CreateWalletRequest createWalletRequest;

    WalletCreationData(KeyStore keyStore, CreateWalletRequest createWalletRequest) {
      this.keyStore = keyStore;
      this.createWalletRequest = createWalletRequest;
    }
  }
}
