package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.SERVICE_NAME_NOOSPHERE_HUB;

import java.util.List;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public abstract class BaseResource<T> {

  protected final DiscoveryClient discoveryClient;
  protected final ReactiveOAuth2AuthorizedClientManager clientManager;
  protected final WebClient.Builder webClientBuilder;
  private final Class<T> typeClass;

  public BaseResource(
    Class<T> typeClass,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    DiscoveryClient discoveryClient,
    WebClient.Builder webClientBuilder
  ) {
    this.typeClass = typeClass;
    this.clientManager = clientManager;
    this.discoveryClient = discoveryClient;
    this.webClientBuilder = webClientBuilder;
  }

  protected String getNoosphereHubServiceUrl() {
    return discoveryClient.getInstances(SERVICE_NAME_NOOSPHERE_HUB).stream()
      .findFirst()
      .map(instance -> instance.getUri().toString())
      .orElseThrow(() -> new IllegalStateException("No available " + SERVICE_NAME_NOOSPHERE_HUB + " service instances"));
  }

  protected Mono<OAuth2AuthorizedClient> authorizedClient(OAuth2AuthenticationToken authentication, ServerWebExchange exchange) {
    String clientRegistrationId = authentication.getAuthorizedClientRegistrationId();
    OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
      .principal(authentication)
      .attribute(ServerWebExchange.class.getName(), exchange)
      .build();
    return clientManager.authorize(request);
  }

  protected Mono<String> getOAuth2Token(ServerWebExchange exchange) {
    return exchange.getPrincipal()
      .cast(OAuth2AuthenticationToken.class)
      .flatMap(authentication -> authorizedClient(authentication, exchange))
      .map(client -> client.getAccessToken().getTokenValue());
  }

  protected Mono<ResponseEntity<List<T>>> executePostReturnList(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .post()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntityList(typeClass)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<T>> executePost(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .post()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(typeClass)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<T>> executePut(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .put()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(typeClass)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<T>> executeGet(
    ServerWebExchange exchange,
    String requestUrl
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .get()
        .uri(requestUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(typeClass)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<String>> executePostReturnString(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .post()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(String.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<String>> executePutReturnString(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .put()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(String.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<String>> executeGetReturnString(
    ServerWebExchange exchange,
    String requestUrl
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .get()
        .uri(requestUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(String.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(responseEntity.getBody())));
  }

  protected Mono<ResponseEntity<Void>> executePostReturnVoid(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .post()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(Void.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(null)));
  }

  protected Mono<ResponseEntity<Void>> executePutReturnVoid(
    ServerWebExchange exchange,
    String requestUrl,
    Object requestBody
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .put()
        .uri(requestUrl)
        .bodyValue(requestBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(Void.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(null)));
  }

  protected Mono<ResponseEntity<Void>> executeDeleteReturnVoid(
    ServerWebExchange exchange,
    String requestUrl
  ) {
    return getOAuth2Token(exchange).flatMap(tokenValue ->
      webClientBuilder.build()
        .delete()
        .uri(requestUrl)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue)
        .retrieve()
        .toEntity(Void.class)
        .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode())
          .headers(responseEntity.getHeaders())
          .body(null)));
  }
}