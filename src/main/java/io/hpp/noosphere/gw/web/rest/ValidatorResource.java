package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.API_URL_SLASH;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_CONTAINERS;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_VALIDATORS;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_SEARCH;

import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.ValidatorDTO;
import io.hpp.noosphere.gw.web.rest.vm.ValidatorDTO;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchValidatorVm;
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
@RequestMapping("/api/validators")
public class ValidatorResource extends BaseResource<ValidatorDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(ValidatorResource.class);

  private static final String ENTITY_NAME = "validator";


  public ValidatorResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder
  ) {
    super(ValidatorDTO.class, clientManager, discoveryClient, webClientBuilder);
  }


  @PostMapping
  public Mono<ResponseEntity<ValidatorDTO>> createValidator(
    @Valid @RequestBody ValidatorDTO validatorDTO,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create Validator : {}", validatorDTO);

    if (validatorDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new validator cannot already have an ID", ENTITY_NAME, "idexists"));
    }

    return Mono.defer(() -> {
      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VALIDATORS;

      return executePost(exchange, requestUrl, validatorDTO)
        .doOnSuccess(result -> LOG.debug("Created validator with ID: {}", result.getBody().getId()));
    });
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<List<ValidatorDTO>>> search(
    @Valid @RequestBody SearchValidatorVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search validators");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VALIDATORS + SERVICE_API_SEARCH;
      requestUrl = UrlUtils.buildRequestUrl(requestUrl, pageable);
      return executePostReturnList(exchange, requestUrl, searchCriteria);
    });
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ValidatorDTO>> getValidator(
    ServerWebExchange exchange,
    @PathVariable("id") UUID id
  ) {
    LOG.debug("REST request to get Validator : {}", id);
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_VALIDATORS + API_URL_SLASH + id.toString();

      return executeGet(exchange, requestUrl);
    });
  }


}
