package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.web.rest.dto.ContainerDTO;
import io.hpp.noosphere.gw.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.gw.web.rest.vm.PageableVm;
import io.hpp.noosphere.gw.web.rest.vm.search.SearchContainerVm;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/containers")
public class ContainerResource {

  private static final Logger LOG = LoggerFactory.getLogger(ContainerResource.class);

  private static final String ENTITY_NAME = "container";

  private final NoosphereHubClient noosphereHubClient;

  public ContainerResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  /**
   * {@code POST /api/containers} : Create a new container.
   *
   * @param containerDTO the container to create.
   * @return the {@link Mono} with status {@code 201 (Created)} and with body the new container, or with status {@code 400 (Bad Request)} if the container has
   * already an ID.
   */
  @PostMapping
  public Mono<ContainerDTO> createContainer(@Valid @RequestBody ContainerDTO containerDTO) {
    LOG.debug("REST request to create Container : {}", containerDTO);
    if (containerDTO.getId() != null) {
      return Mono.error(new BadRequestAlertException("A new container cannot already have an ID", ENTITY_NAME, "idexists"));
    }
    // The client call returns a Mono<ContainerDTO>, which we map to a ResponseEntity.
    return noosphereHubClient.createContainer(containerDTO);
  }

  /**
   * {@code GET /api/containers/:id} : get the "id" container.
   *
   * @param id the id of the container to retrieve.
   * @return the {@link Mono} with status {@code 200 (OK)} and with body the container, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public Mono<ContainerDTO> getContainer(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get Container : {}", id);
    return noosphereHubClient.getContainer(id);
  }

  /**
   * {@code DELETE /api/containers/:id} : delete the "id" container.
   *
   * @param id the id of the container to delete.
   * @return the {@link Mono} with status {@code 204 (No Content)}, or status {@code 404 (Not Found)}.
   */
  @DeleteMapping("/{id}")
  public Mono<Void> deleteContainer(@PathVariable("id") UUID id) {
    LOG.debug("REST request to delete Container : {}", id);
    return noosphereHubClient.deleteContainer(id);
  }

  /**
   * {@code POST /api/containers/search} : search for containers.
   *
   * @param searchCriteria the criteria to search by.
   * @param pageable       the pagination information.
   * @param exchange       the server web exchange, for building pagination headers.
   * @return the {@link Mono} with status {@code 200 (OK)} and with body a list of containers, and pagination headers.
   */
  @PostMapping("/search")
  public Flux<ContainerDTO> searchContainers(
    @Valid @RequestBody SearchContainerVm searchCriteria,
    Pageable pageable,
    ServerWebExchange exchange
  ) {
    LOG.debug("REST request to search for a page of Containers with criteria: {}", searchCriteria);
    // The Feign client returns a Flux of containers. We collect them into a list.
    // Note: With this client signature, we cannot relay pagination headers from the downstream service.
    return noosphereHubClient.searchContainers(searchCriteria, new PageableVm(pageable));
  }
}