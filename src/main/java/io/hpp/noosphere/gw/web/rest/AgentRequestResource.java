package io.hpp.noosphere.gw.web.rest;

import static io.hpp.noosphere.gw.config.Constants.API_URL_SLASH;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_AGENT_REQUESTS;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_PREFIX;
import static io.hpp.noosphere.gw.config.Constants.SERVICE_API_SEARCH;

import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.AgentRequestDTO;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentRequestVm;
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
@RequestMapping("/api/agent-requests")
public class AgentRequestResource extends BaseResource<AgentRequestDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(AgentRequestResource.class);

  private static final String ENTITY_NAME = "agentRequest";


  public AgentRequestResource(
    DiscoveryClient discoveryClient,
    ReactiveOAuth2AuthorizedClientManager clientManager,
    WebClient.Builder webClientBuilder
  ) {
    super(AgentRequestDTO.class, clientManager, discoveryClient, webClientBuilder);
  }

  @PostMapping
  public Mono<ResponseEntity<AgentRequestDTO>> createAgentRequest(
    @Valid @RequestBody AgentRequestDTO agentRequestDTO,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to create AgentRequest : {}", agentRequestDTO);

    if (agentRequestDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new agentRequest cannot already have an ID", ENTITY_NAME, "idexists"));
    }

    return Mono.defer(() -> {
      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_AGENT_REQUESTS;

      return executePost(exchange, requestUrl, agentRequestDTO)
        .doOnSuccess(result -> LOG.debug("Created agentRequest with ID: {}", result.getBody().getId()));
    });
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<List<AgentRequestDTO>>> search(
    @Valid @RequestBody SearchAgentRequestVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search AgentRequests");
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_AGENT_REQUESTS + SERVICE_API_SEARCH;
      requestUrl = UrlUtils.buildRequestUrl(requestUrl, pageable);
      return executePostReturnList(exchange, requestUrl, searchCriteria);
    });
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<AgentRequestDTO>> getAgentRequest(
    ServerWebExchange exchange,
    @PathVariable("id") UUID id
  ) {
    LOG.debug("REST request to get AgentRequest : {}", id);
    return Mono.defer(() -> {

      String serviceUrl = getNoosphereHubServiceUrl();
      String requestUrl = serviceUrl + SERVICE_API_PREFIX + SERVICE_API_AGENT_REQUESTS + API_URL_SLASH + id.toString();

      return executeGet(exchange, requestUrl);
    });
  }

}