package io.hpp.noosphere.gw.config;

import io.hpp.noosphere.gw.security.apikey.ApiKeyAuthenticationConverter;
import io.hpp.noosphere.gw.security.apikey.ApiKeyAuthenticationToken;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

public class ApiKeyRelayRequestInterceptor implements ReactiveHttpRequestInterceptor {

    public ApiKeyRelayRequestInterceptor() {
        // Empty constructor
    }

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
        return ReactiveSecurityContextHolder
            .getContext()
            .flatMap(securityContext -> {
                Authentication authentication = securityContext.getAuthentication();
                if (authentication instanceof ApiKeyAuthenticationToken) {
                    request
                        .headers()
                        .put(
                            ApiKeyAuthenticationConverter.API_KEY_HEADER,
                            Collections.singletonList((String) authentication.getCredentials())
                        );
                } else if (authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                    String accessToken = jwtAuth.getToken().getTokenValue();
                    request.headers().put(HttpHeaders.AUTHORIZATION, Collections.singletonList("Bearer " + accessToken));
                }
                return Mono.just(request);
            })
            .defaultIfEmpty(request);
    }
}
