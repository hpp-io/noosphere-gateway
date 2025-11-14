package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.API_URL_SLASH;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_VERIFIERS;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_SEARCH;

import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.VerifierDTO;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchVerifierVm;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/verifiers")
public class VerifierResource extends BaseResource<VerifierDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(VerifierResource.class);

  private static final String ENTITY_NAME = "verifier";


  public VerifierResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder
  ) {
    super(VerifierDTO.class, clientManager, discoveryClient, webClientBuilder);
  }


  @PostMapping
  public Mono<ResponseEntity<VerifierDTO>> createVerifier(
    @Valid @RequestBody VerifierDTO verifierDTO,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create Verifier : {}", verifierDTO);

    if (verifierDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new verifier cannot already have an ID", ENTITY_NAME, "idexists"));
    }

    return Mono.defer(() -> {
      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VERIFIERS;

      return executePost(exchange, requestUrl, verifierDTO)
        .doOnSuccess(result -> LOG.debug("Created verifier with ID: {}", result.getBody().getId()));
    });
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<List<VerifierDTO>>> search(
    @Valid @RequestBody SearchVerifierVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search verifiers");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VERIFIERS + SERVICE_API_SEARCH;
      requestUrl = UrlUtils.buildRequestUrl(requestUrl, pageable);
      return executePostReturnList(exchange, requestUrl, searchCriteria);
    });
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<VerifierDTO>> getVerifier(
    ServerWebExchange exchange,
    @PathVariable("id") UUID id
  ) {
    LOG.debug("REST request to get Verifier : {}", id);
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VERIFIERS + API_URL_SLASH + id.toString();

      return executeGet(exchange, requestUrl);
    });
  }


}
