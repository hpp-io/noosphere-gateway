package io.hpp.noosphere.gw.config;

import java.util.Collections;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

@Component
public class OAuth2FeignRequestInterceptor implements ReactiveHttpRequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final ReactiveOAuth2AuthorizedClientService clientService;

    public OAuth2FeignRequestInterceptor(ReactiveOAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest request) {
        return ReactiveSecurityContextHolder
            .getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(authentication -> {
                if (authentication instanceof OAuth2AuthenticationToken) {
                    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                    String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
                    return clientService
                        .loadAuthorizedClient(clientRegistrationId, oauthToken.getName())
                        .map(client -> {
                            OAuth2AccessToken accessToken = client.getAccessToken();
                            if (accessToken != null) {
                                request.headers()
                                    .put(
                                        AUTHORIZATION_HEADER,
                                        Collections.singletonList(String.format("%s %s", BEARER_TOKEN_TYPE, accessToken.getTokenValue()))
                                    );
                            }
                            return request;
                        })
                        .defaultIfEmpty(request);
                }
                return Mono.just(request);
            })
            .defaultIfEmpty(request);
    }
}
