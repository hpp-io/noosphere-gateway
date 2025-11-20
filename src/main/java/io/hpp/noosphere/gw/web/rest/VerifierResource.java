package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.VerifierDTO;
import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchVerifierVm;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/verifiers")
public class VerifierResource {

  private static final Logger LOG = LoggerFactory.getLogger(VerifierResource.class);

  private static final String ENTITY_NAME = "verifier";

  private final NoosphereHubClient noosphereHubClient;

  public VerifierResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PostMapping
  public Mono<VerifierDTO> createVerifier(@Valid @RequestBody VerifierDTO verifierDTO) {
    LOG.debug("REST request to create Verifier : {}", verifierDTO);
    if (verifierDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new verifier cannot already have an ID", ENTITY_NAME, "idexists"));
    }
    return noosphereHubClient.createVerifier(verifierDTO);
  }

  @PostMapping("/search")
  public Flux<VerifierDTO> searchVerifiers(
    @Valid @RequestBody SearchVerifierVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search verifiers");
    return noosphereHubClient.searchVerifiers(searchCriteria, new PageableVm(pageable));
  }

  @GetMapping("/{id}")
  public Mono<VerifierDTO> getVerifier(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get Verifier : {}", id);
    return noosphereHubClient.getVerifier(id);
  }
}