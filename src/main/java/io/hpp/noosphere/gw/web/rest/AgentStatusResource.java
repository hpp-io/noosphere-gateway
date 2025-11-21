package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.web.rest.vm.KeepAliveResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agents")
public class AgentStatusResource {

  private final NoosphereHubClient noosphereHubClient;

  public AgentStatusResource(NoosphereHubClient noosphereHubClient) {
    this.noosphereHubClient = noosphereHubClient;
  }

  @PutMapping("/{agentId}/keep-alive")
  @RateLimited
  public Mono<KeepAliveResponse> keepAlive(@PathVariable("agentId") UUID agentId) {
    return noosphereHubClient.keepAlive(agentId);
  }
}
