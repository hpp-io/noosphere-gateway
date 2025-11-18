package io.hpp.noosphere.gw.web.rest;

import feign.FeignException;
import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserResource {

  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private final NoosphereHubClient noosphereHubClient;

  public UserResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PostMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> createWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to create wallet");
    return noosphereHubClient
      .createWallet(updateWalletVm)
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @PutMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> updateWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to update wallet");
    return noosphereHubClient
      .updateWallet(updateWalletVm)
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @GetMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> getWallet() {
    LOG.debug("REST request to get wallet");
    return noosphereHubClient
      .getWallet()
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @GetMapping("/mine/api-key")
  public Mono<ResponseEntity<String>> getApiKey() {
    LOG.debug("REST request to get apiKey");
    return noosphereHubClient
      .getApiKey()
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @PostMapping("/mine/api-key")
  public Mono<ResponseEntity<String>> createApiKey() {
    LOG.debug("REST request to create apiKey");
    return noosphereHubClient
      .createApiKey()
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @GetMapping("/profile")
  public Mono<ResponseEntity<UserDTO>> getUserProfile() {
    LOG.debug("REST request to get User Profile");
    return noosphereHubClient
      .getUserProfile()
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }

  @PutMapping("/profile")
  public Mono<ResponseEntity<Void>> updateUserProfile(@RequestBody UserDTO userDTO) {
    LOG.debug("REST request to update User Profile");
    return noosphereHubClient
      .updateUserProfile(userDTO)
      .then(Mono.just(ResponseEntity.ok().<Void>build()))
      .onErrorResume(FeignException.class, e -> Mono.just(ResponseEntity.status(e.status()).build()));
  }
}