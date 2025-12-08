package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.common.config.Constants.KEY_ALIAS_HPP_WALLET_ADDRESS;

import io.hpp.noosphere.common.security.KeystoreManager;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.security.SecurityUtils;
import io.hpp.noosphere.gw.service.UserService;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.dto.KeystoreRequest;
import io.hpp.noosphere.gw.web.rest.vm.CreateWalletVm;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import io.hpp.noosphere.gw.web.rest.vm.WalletCreationVm;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/users")
public class UserResource {

  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private final UserService userService;
  private final NoosphereHubClient noosphereHubClient;

  public UserResource(
    UserService userService,
    NoosphereHubClient noosphereHubClient
  ) {
    this.userService = userService;
    this.noosphereHubClient = noosphereHubClient;
  }

  @PostMapping("/mine/wallet")
  @RateLimited
  public Mono<String> createMyWallet(@Valid @RequestBody CreateWalletVm createWalletVm) {
    LOG.debug("REST request to create wallet");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.createMyWallet(userId, createWalletVm.getOwnerAddress(), now));
  }

  @PutMapping("/mine/wallet")
  @RateLimited
  public Mono<Void> updateMyWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to update wallet");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.updateMyWalletAndPropagate(userId, updateWalletVm.getWalletAddress(), now));
  }

  @GetMapping("/mine/wallet")
  @RateLimited
  public Mono<ResponseEntity<String>> getMyWallet() {
    LOG.debug("REST request to get wallet");
    return SecurityUtils.getCurrentUserId()
      .map(userService::findById)
      .flatMap(user -> {
        LOG.info("User found: {}", user);
        return Mono.justOrEmpty(user.getWalletAddress());
      })
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.ok("")); // If NO key, return 200 OK with empty string
  }

  @GetMapping("/mine/api-key")
  @RateLimited
  public Mono<ResponseEntity<String>> getMyApiKey() {
    LOG.debug("REST request to get apiKey");
    return SecurityUtils.getCurrentUserId()
      .map(userService::findById)
      .flatMap(user -> {
        LOG.info("User found: {}", user);
        return Mono.justOrEmpty(user.getApiKey());
      })
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.ok("")); // If NO key, return 200 OK with empty string
  }

  @PostMapping("/mine/api-key")
  @RateLimited
  public Mono<String> createApiKey() {
    LOG.debug("REST request to create apiKey");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.createAndUpdateMyApiKey(userId, now));
  }


  @PostMapping("/mine/keystore")
  @RateLimited
  public Mono<Resource> createKeystore(@RequestBody KeystoreRequest request) {
    LOG.debug("REST request to create keystore");
    Instant now = Instant.now();
    Mono<Path> tempFileMono = Mono
      .fromCallable(() -> Files.createTempFile("keystore", ".p12"))
      .subscribeOn(Schedulers.boundedElastic());

    Mono<Path> keystoreMono;
    if (Boolean.TRUE.equals(request.getIsWallet())) {
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
              CreateWalletVm createWalletVm = new CreateWalletVm();
              createWalletVm.setOwnerAddress(credentials.getAddress());
              return new WalletCreationVm(ks, createWalletVm, request.getWalletAddress());
            })
            .flatMap(walletData -> {
                if (CommonUtils.isValid(walletData.getWalletAddress())) {
                  return Mono
                    .fromCallable(() -> {
                      try {
                        KeystoreManager.addSecretKeyWithUtf8String(
                          walletData.getKeyStore(),
                          request.getPassword(),
                          KEY_ALIAS_HPP_WALLET_ADDRESS,
                          walletData.getWalletAddress()
                        );
                        KeystoreManager.saveKeyStore(
                          walletData.getKeyStore(),
                          tempFile,
                          request.getPassword()
                        );
                        return tempFile;
                      } catch (GeneralSecurityException | IOException e) {
                        throw new RuntimeException(e);
                      }
                    })
                    .subscribeOn(Schedulers.boundedElastic());
                } else {
                  return noosphereHubClient
                    .createMyWallet(walletData.getCreateWalletVm())
                    .switchIfEmpty(Mono.error(new IOException("Failed to create HPP wallet in Noosphere Hub.")))
                    .flatMap(newHppWallet ->
                      SecurityUtils
                        .getCurrentUserId()
                        .flatMap(userId ->
                          Mono
                            .fromCallable(() -> {
                              try {
                                userService.updateMyWalletAddress(userId, newHppWallet, now);
                                KeystoreManager.addSecretKeyWithUtf8String(
                                  walletData.getKeyStore(),
                                  request.getPassword(),
                                  KEY_ALIAS_HPP_WALLET_ADDRESS,
                                  newHppWallet
                                );
                                KeystoreManager.saveKeyStore(
                                  walletData.getKeyStore(),
                                  tempFile,
                                  request.getPassword()
                                );
                                return tempFile;
                              } catch (GeneralSecurityException | IOException e) {
                                throw new RuntimeException(e);
                              }
                            })
                        )
                        .subscribeOn(Schedulers.boundedElastic())
                    );
                }
              }
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
      .filter(Objects::nonNull)
      .flatMap(path ->
        Mono.fromCallable(() -> {
          ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
          Files.delete(path); // Clean up the temporary file
          return (Resource) resource;
        }).subscribeOn(Schedulers.boundedElastic())
      )
      .doOnError(e -> LOG.error("Failed to create keystore", e));
  }

  @GetMapping("/profile")
  @RateLimited
  public Mono<UserDTO> getUserProfile() {
    LOG.debug("REST request to get User Profile");
    return SecurityUtils.getCurrentUserId()
      .map(userService::findById);
  }

  @PutMapping("/profile")
  @RateLimited
  public Mono<Void> updateUserProfile(@RequestBody UserDTO userDTO) {
    LOG.debug("REST request to update User Profile");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.updateUserProfile(
        userId,
        userDTO.getFirstName(),
        userDTO.getLastName(),
        userDTO.getLangKey(),
        userDTO.getImageUrl(),
        now
      ));
  }
}
