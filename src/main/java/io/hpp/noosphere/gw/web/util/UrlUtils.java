package io.hpp.noosphere.gw.web.util;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

public class UrlUtils {


  public static String buildRequestUrl(String baseUrl, Pageable pageable) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl);

    // Add pagination parameters
    uriBuilder
      .queryParam("page", pageable.getPageNumber())
      .queryParam("size", pageable.getPageSize());

    // Add sort parameters, if any
    pageable.getSort().forEach(order ->
      uriBuilder.queryParam("sort", order.getProperty() + "," + order.getDirection().name())
    );

    return uriBuilder.toUriString();
  }


}
