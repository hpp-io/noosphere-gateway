package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.config.ApplicationProperties;
import io.hpp.noosphere.gw.config.RateLimited;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/server")
public class ServerInfoResource {

  private final ApplicationProperties applicationProperties;

  public ServerInfoResource(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  @GetMapping("/info")
  @RateLimited
  public ServerInfoVM getServerInfo() {
    return new ServerInfoVM(applicationProperties.getAppKit().getProjectId(), applicationProperties.getEnvironment());
  }

  @Data
  @AllArgsConstructor
  class ServerInfoVM {

    private String projectId;
    private String envValue;
  }
}
