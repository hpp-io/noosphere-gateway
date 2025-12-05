package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.security.SecurityUtils;
import io.hpp.noosphere.gw.service.UserService;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import jakarta.validation.Valid;
import java.time.Instant;
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

  private final UserService userService;

  public UserResource(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/mine/wallet")
  @RateLimited
  public Mono<String> createMyWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to create wallet");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.createAndUpdateMyWallet(userId, updateWalletVm.getOwnerAddress(), now));
  }

  @PutMapping("/mine/wallet")
  @RateLimited
  public Mono<String> updateMyWallet(@Valid @RequestBody UpdateWalletVm updateWalletVm) {
    LOG.debug("REST request to update wallet");
    Instant now = Instant.now();
    return SecurityUtils.getCurrentUserId()
      .flatMap(userId -> userService.updateWithNewMyWallet(userId, updateWalletVm.getOwnerAddress(), now));
  }

  @GetMapping("/mine/wallet")
  @RateLimited
  public Mono<ResponseEntity<String>> getMyWallet() {
    LOG.debug("REST request to get wallet");
    return SecurityUtils.getCurrentUserId()
      .map(userService::findById)
      .flatMap(user -> Mono.justOrEmpty(user.getWalletAddress()))
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.ok("")); // If NO key, return 200 OK with empty string
  }

  @GetMapping("/mine/api-key")
  @RateLimited
  public Mono<ResponseEntity<String>> getMyApiKey() {
    LOG.debug("REST request to get apiKey");
    return SecurityUtils.getCurrentUserId()
      .map(userService::findById)
      .flatMap(user -> Mono.justOrEmpty(user.getApiKey()))
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
