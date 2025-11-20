package io.hpp.noosphere.gw.client;

import io.hpp.noosphere.gw.config.ApiKeyRelayRequestInterceptor;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.web.server.ServerWebExchange;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.webclient.WebReactiveOptions;
import reactor.core.publisher.Mono;

@Configuration
public class NoosphereHubFeignConfiguration {

    @Bean
    public WebReactiveOptions noosphereHubReactiveOptions() {
        return new WebReactiveOptions.Builder().setReadTimeoutMillis(90000).setConnectTimeoutMillis(90000).build();
    }

    @Bean
    public ReactiveHttpRequestInterceptor apiKeyRelayRequestInterceptor() {
        return new ApiKeyRelayRequestInterceptor();
    }

    @Bean
    public ReactiveHttpRequestInterceptor csrfTokenRelayRequestInterceptor() {
        return request ->
            Mono.deferContextual(
                contextView -> {
                    if (contextView.hasKey(ServerWebExchange.class)) {
                        ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                        HttpCookie xsrfTokenCookie = exchange.getRequest().getCookies().getFirst("XSRF-TOKEN");
                        if (xsrfTokenCookie != null) {
                            request.headers().put("X-XSRF-TOKEN", Collections.singletonList(xsrfTokenCookie.getValue()));
                        }
                    }
                    return Mono.just(request);
                }
            );
    }
}
