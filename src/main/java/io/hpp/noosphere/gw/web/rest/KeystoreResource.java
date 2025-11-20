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
  public Mono<Resource> createKeystore(@RequestBody KeystoreRequest request) {
    Mono<Path> tempFileMono = Mono
      .fromCallable(() -> Files.createTempFile("keystore", ".p12"))
      .subscribeOn(Schedulers.boundedElastic());

    Mono<Path> keystoreMono;
    if (Boolean.TRUE.equals(request.getIsWallet()) && Boolean.TRUE.equals(request.getCreateHppWallet())) {
      keystoreMono =
        tempFileMono.flatMap(tempFile ->
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
            .flatMap(walletData ->
              noosphereHubClient
                .createWallet(walletData.createWalletRequest)
                .switchIfEmpty(Mono.error(new IOException("Failed to create HPP wallet in Noosphere Hub.")))
                .flatMap(newHppWallet -> {
                  try {
                    KeystoreManager.addSecretKeyWithUtf8String(
                      walletData.keyStore,
                      request.getPassword(),
                      KEY_ALIAS_HPP_WALLET_ADDRESS,
                      newHppWallet
                    );
                    KeystoreManager.saveKeyStore(walletData.keyStore, tempFile, request.getPassword());
                    return Mono.just(tempFile);
                  } catch (GeneralSecurityException | IOException e) {
                    return Mono.error(e);
                  }
                })
            )
        );
    } else {
      keystoreMono =
        tempFileMono.flatMap(tempFile ->
          Mono.fromRunnable(() -> {
              try {
                if (Boolean.TRUE.equals(request.getIsWallet())) {
                  KeystoreManager.createKeyStoreWithHexPrivateKey(
                    tempFile,
                    request.getPassword(),
                    request.getKeyAlias(),
                    request.getPrivateKey()
                  );
                } else {
                  KeystoreManager.createKeyStoreWithUtf8String(
                    tempFile,
                    request.getPassword(),
                    request.getKeyAlias(),
                    request.getPrivateKey()
                  );
                }
              } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
              }
            })
            .thenReturn(tempFile)
        );
    }

    return keystoreMono
      .flatMap(path ->
        Mono.fromCallable(() -> {
          ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
          Files.delete(path); // Clean up the temporary file
          return (Resource) resource;
        }).subscribeOn(Schedulers.boundedElastic())
      )
      .doOnError(e -> log.error("Failed to create keystore", e));
  }

  @PostMapping("/read")
  public Mono<String> readKeystore(@RequestBody KeystoreReadRequest request) {
    return Mono
      .fromCallable(() -> {
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
        return value;
      })
      .subscribeOn(Schedulers.boundedElastic())
      .doOnError(e -> log.error("Failed to read keystore", e));
  }

  private record WalletCreationData(KeyStore keyStore, CreateWalletRequest createWalletRequest) {

  }
}
