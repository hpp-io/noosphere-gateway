package io.hpp.noosphere.gw.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.webclient.WebReactiveOptions;

@Configuration
public class NoosphereHubFeignConfiguration {

    @Bean
    public WebReactiveOptions noosphereHubReactiveOptions() {
        return new WebReactiveOptions.Builder()
                .setReadTimeoutMillis(90000)
                .setConnectTimeoutMillis(90000)
                .build();
    }
}
