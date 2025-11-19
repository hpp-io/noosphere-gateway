package io.hpp.noosphere.gw.security.apikey;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticationWebFilter extends AuthenticationWebFilter {

    public ApiKeyAuthenticationWebFilter(
        ReactiveAuthenticationManager authenticationManager,
        ServerAuthenticationConverter authenticationConverter
    ) {
        super(authenticationManager);
        setServerAuthenticationConverter(authenticationConverter);
    }
}
