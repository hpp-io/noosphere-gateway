package io.hpp.noosphere.gw.client;

import io.hpp.noosphere.gw.config.ApiKeyRelayRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.webclient.WebReactiveOptions;

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
}
