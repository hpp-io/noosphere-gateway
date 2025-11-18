package io.hpp.noosphere.gw.web.rest;

import feign.FeignException;
import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.AgentRequestDTO;
import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchAgentRequestVm;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agent-requests")
public class AgentRequestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AgentRequestResource.class);

  private static final String ENTITY_NAME = "agentRequest";

  private final NoosphereHubClient noosphereHubClient;

  public AgentRequestResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PostMapping
  public Mono<ResponseEntity<AgentRequestDTO>> createAgentRequest(@Valid @RequestBody AgentRequestDTO agentRequestDTO) {
    LOG.debug("REST request to create AgentRequest : {}", agentRequestDTO);
    if (agentRequestDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new agentRequest cannot already have an ID", ENTITY_NAME, "idexists"));
    }
    return noosphereHubClient
      .createAgentRequest(agentRequestDTO)
      .map(createdDto -> {
        LOG.debug("Created agentRequest with ID: {}", createdDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
      })
      .onErrorResume(FeignException.InternalServerError.class, e -> Mono.just(ResponseEntity.internalServerError().build()))
      .onErrorResume(FeignException.BadRequest.class, e -> Mono.just(ResponseEntity.badRequest().build()));
  }

  @PostMapping("/search")
  public Mono<ResponseEntity<List<AgentRequestDTO>>> searchAgentRequests(
    @Valid @RequestBody SearchAgentRequestVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search AgentRequests");
    return noosphereHubClient
      .searchAgentRequests(searchCriteria, new PageableVm(pageable))
      .collectList()
      .map(ResponseEntity::ok)
      .onErrorResume(
        FeignException.Unauthorized.class,
        e -> {
          LOG.error("Unauthorized error during agentRequest search", e);
          return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).<List<AgentRequestDTO>>build());
        }
      )
      .onErrorResume(FeignException.BadRequest.class, e -> Mono.just(ResponseEntity.badRequest().<List<AgentRequestDTO>>build()))
      .onErrorResume(FeignException.InternalServerError.class, e -> Mono.just(ResponseEntity.internalServerError().<List<AgentRequestDTO>>build()));
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<AgentRequestDTO>> getAgentRequest(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get AgentRequest : {}", id);
    return noosphereHubClient
      .getAgentRequest(id)
      .map(ResponseEntity::ok)
      .onErrorResume(FeignException.NotFound.class, e -> {
        LOG.debug("AgentRequest not found from downstream service for ID: {}", id);
        return Mono.just(ResponseEntity.notFound().build());
      })
      .onErrorResume(FeignException.InternalServerError.class, e -> Mono.just(ResponseEntity.internalServerError().build()));
  }
}