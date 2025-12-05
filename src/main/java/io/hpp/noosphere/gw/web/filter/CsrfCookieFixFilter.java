package io.hpp.noosphere.gw.web.filter;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsrfCookieFixFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return chain.filter(exchange).doOnSuccess(aVoid -> {
      // Hook into the response before it is committed
      exchange.getResponse().beforeCommit(() -> {
        // Check if the XSRF-TOKEN cookie exists in the response
        List<ResponseCookie> cookies = exchange.getResponse().getCookies().get("XSRF-TOKEN");

        if (cookies != null && !cookies.isEmpty()) {
          // Rebuild the cookies with "SameSite=Lax" explicitly set
          List<ResponseCookie> fixedCookies = cookies.stream()
            .map(original -> {
              // If SameSite is missing/null, we reconstruct the cookie
              if (original.getSameSite() == null) {
                return ResponseCookie.from(original.getName(), original.getValue())
                  .domain(original.getDomain())
                  .path(original.getPath())
                  .maxAge(original.getMaxAge())
                  .secure(original.isSecure())
                  .httpOnly(original.isHttpOnly())
                  .sameSite("Lax") // <--- THE FIX
                  .build();
              }
              return original;
            })
            .collect(Collectors.toList());

          // Replace the old cookies with the fixed ones
          exchange.getResponse().getCookies().put("XSRF-TOKEN", fixedCookies);
        }
        return Mono.empty();
      });
    });
  }
}
