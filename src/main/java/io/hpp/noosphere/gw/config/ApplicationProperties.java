package io.hpp.noosphere.gw.config;

import io.hpp.noosphere.common.config.SharedApplicationProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Noosphere Gateway.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties extends SharedApplicationProperties {
    // jhipster-needle-application-properties-property

    // jhipster-needle-application-properties-property-getter

    // jhipster-needle-application-properties-property-class

  private AppKit appKit = new AppKit();
  private String environment;
  private Storage storage = new Storage();

  @Data
  public static class Storage {

    private String basePath;

  }

  @Data
  public static class AppKit {

    private String projectId;

  }
}
