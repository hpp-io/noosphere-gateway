package io.hpp.noosphere.gw.security.apikey;

import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.security.AuthoritiesConstants;
import io.hpp.noosphere.gw.web.rest.dto.UserDTO;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    private final NoosphereHubClient noosphereHubClient;

    public ApiKeyAuthenticationManager(NoosphereHubClient noosphereHubClient) {
        this.noosphereHubClient = noosphereHubClient;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
            .cast(ApiKeyAuthenticationToken.class)
            .map(ApiKeyAuthenticationToken::getCredentials)
            .cast(String.class)
            .flatMap(apiKey -> noosphereHubClient.getUserFromApiKey(apiKey)
                .map(userDTO -> new ApiKeyAuthenticationToken(
                    userDTO.getApiKey(),
                    Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER))
                ))
            );
    }
}
