package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.API_URL_SLASH;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_CONTAINERS;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_SEARCH;

import io.hpp.noosphere.gw.web.rest.vm.ContainerDTO;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchContainerVm;
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
@RequestMapping("/api/containers")
public class ContainerResource extends BaseResource<ContainerDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(ContainerResource.class);

  private static final String ENTITY_NAME = "container";


  public ContainerResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder
  ) {
    super(ContainerDTO.class, clientManager, discoveryClient, webClientBuilder);
  }


  @PostMapping("/search")
  public Mono<ResponseEntity<List<ContainerDTO>>> search(
    @Valid @RequestBody SearchContainerVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search containers");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_CONTAINERS + SERVICE_API_SEARCH;
      requestUrl = UrlUtils.buildRequestUrl(requestUrl, pageable);
      return executePostReturnList(exchange, requestUrl, searchCriteria);
    });
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ContainerDTO>> getContainer(
    ServerWebExchange exchange,
    @PathVariable("id") UUID id
  ) {
    LOG.debug("REST request to get Container : {}", id);
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_CONTAINERS + API_URL_SLASH + id.toString();

      return executeGet(exchange, requestUrl);
    });
  }


}
