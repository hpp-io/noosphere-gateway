package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_MINE_API_KEY;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_MINE_WALLET;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_USER_PROFILE;

import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import io.hpp.noosphere.gw.web.rest.vm.UserDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserResource extends BaseResource<UserDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private static final String ENTITY_NAME = "user";

  public UserResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder
  ) {
    super(UserDTO.class, clientManager, discoveryClient, webClientBuilder);
  }


  @PostMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> createWallet(
    @Valid @RequestBody UpdateWalletVm updateWalletVm,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create wallet");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET;

      return executePostReturnString(exchange, requestUrl, updateWalletVm);
    });
  }

  @PutMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> updateWallet(
    @Valid @RequestBody UpdateWalletVm updateWalletVm,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to update wallet");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET;

      return executePutReturnString(exchange, requestUrl, updateWalletVm);
    });
  }

  @GetMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> getWallet(
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to get wallet");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();

      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET;

      return executeGetReturnString(exchange, requestUrl);
    });

  }


  @GetMapping("/mine/api-key")
  public Mono<ResponseEntity<String>> getApiKey(
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to get apiKey");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_API_KEY;

      return executeGetReturnString(exchange, requestUrl);
    });

  }

  @PostMapping("/mine/api-key")
  public Mono<ResponseEntity<String>> createApiKey(
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create apiKey");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_API_KEY;

      return executePostReturnString(exchange, requestUrl, new UserDTO());
    });

  }

  @GetMapping("/profile")
  public Mono<ResponseEntity<UserDTO>>getUserProfile(
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to get User Profile");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_USER_PROFILE;

      return executeGet(exchange, requestUrl);
    });
  }

  @PutMapping("/profile")
  public Mono<ResponseEntity<Void>> updateUserProfile(
    ServerWebExchange exchange,
    @RequestBody UserDTO userDTO
  ) {
    LOG.debug("REST request to update User Profile");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_USER_PROFILE;

      return executePutReturnVoid(exchange, requestUrl, userDTO);
    });
  }

}
