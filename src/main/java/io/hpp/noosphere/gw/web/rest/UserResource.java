package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  public Mono<String> createWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to create wallet");
    return noosphereHubClient.createWallet(updateWalletVm);
  }

  @PutMapping("/mine/wallet")
  public Mono<String> updateWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to update wallet");
    return noosphereHubClient.updateWallet(updateWalletVm);
  }

  @GetMapping("/mine/wallet")
  public Mono<String> getWallet() {
    LOG.debug("REST request to get wallet");
    return noosphereHubClient.getWallet();
  }

  @GetMapping("/mine/api-key")
  public Mono<String> getApiKey() {
    LOG.debug("REST request to get apiKey");
    return noosphereHubClient.getApiKey();
  }

  @PostMapping("/mine/api-key")
  public Mono<String> createApiKey() {
    LOG.debug("REST request to create apiKey");
    return noosphereHubClient.createApiKey();
  }

  @GetMapping("/profile")
  public Mono<UserDTO> getUserProfile() {
    LOG.debug("REST request to get User Profile");
    return noosphereHubClient.getUserProfile();
  }

  @PutMapping("/profile")
  public Mono<Void> updateUserProfile(@RequestBody UserDTO userDTO) {
    LOG.debug("REST request to update User Profile");
    return noosphereHubClient.updateUserProfile(userDTO);
  }
}