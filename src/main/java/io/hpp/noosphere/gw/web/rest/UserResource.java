package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_MINE_WALLET;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_NAME_NOOSPHERE_HUB;

import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
public class UserResource {

  private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

  private static final String ENTITY_NAME = "user";

  private final DiscoveryClient discoveryClient;
  private final WebClient.Builder webClientBuilder;
  private final ReactiveOAuth2AuthorizedClientManager clientManager;


  public UserResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder

  ) {
    this.discoveryClient = discoveryClient;
    this.clientManager = clientManager;
    this.webClientBuilder = webClientBuilder;
  }


  private Mono<OAuth2AuthorizedClient> authorizedClient(OAuth2AuthenticationToken authentication, ServerWebExchange exchange) {
    String clientRegistrationId = authentication.getAuthorizedClientRegistrationId();
    OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
      .principal(authentication)
      .attribute(ServerWebExchange.class.getName(), exchange)
      .build();
    return clientManager.authorize(request);
  }

  private String getServiceUrl() {
    return discoveryClient.getInstances(SERVICE_NAME_NOOSPHERE_HUB).stream()
      .findFirst()
      .map(instance -> instance.getUri().toString())
      .orElseThrow(() -> new IllegalStateException("No available " + SERVICE_NAME_NOOSPHERE_HUB + " service instances"));
  }


  @PostMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> createWallet(
    @Valid @RequestBody UpdateWalletVm updateWalletVm,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create wallet");
    return Mono.defer(() -> {

      String serviceUrl = getServiceUrl();

      return exchange.getPrincipal()
        .cast(OAuth2AuthenticationToken.class)
        .flatMap(authentication -> authorizedClient(authentication, exchange))
        .flatMap(client -> {
          String tokenValue = client.getAccessToken().getTokenValue();
          return webClientBuilder.build()
            .post()
            .uri(serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET)
            .bodyValue(updateWalletVm)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
            .retrieve()
            .bodyToMono(String.class)
            .map(walletAddress -> ResponseEntity.ok().body(walletAddress));
        });
    });
  }

  @PutMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> updateWallet(
    @Valid @RequestBody UpdateWalletVm updateWalletVm,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to update wallet");
    return Mono.defer(() -> {

      String serviceUrl = getServiceUrl();

      return exchange.getPrincipal()
        .cast(OAuth2AuthenticationToken.class)
        .flatMap(authentication -> authorizedClient(authentication, exchange))
        .flatMap(client -> {
          String tokenValue = client.getAccessToken().getTokenValue();
          return webClientBuilder.build()
            .put()
            .uri(serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET)
            .bodyValue(updateWalletVm)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
            .retrieve()
            .bodyToMono(String.class)
            .map(walletAddress -> ResponseEntity.ok().body(walletAddress));
        });
    });
  }

  @GetMapping("/mine/wallet")
  public Mono<ResponseEntity<String>> getWallet(
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to get wallet");
    return Mono.defer(() -> {

      String serviceUrl = getServiceUrl();

      return exchange.getPrincipal()
        .cast(OAuth2AuthenticationToken.class)
        .flatMap(authentication -> authorizedClient(authentication, exchange))
        .flatMap(client -> {
          String tokenValue = client.getAccessToken().getTokenValue();
          return webClientBuilder.build()
            .get()
            .uri(serviceUrl + SERVICE_API_PREFIX + SERVICE_API_MINE_WALLET)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
            .retrieve()
            .bodyToMono(String.class)
            .map(walletAddress -> ResponseEntity.ok().body(walletAddress));
        });
    });

  }

}
