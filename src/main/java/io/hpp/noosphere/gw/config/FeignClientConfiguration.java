package io.hpp.noosphere.gw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;

@Configuration
public class FeignClientConfiguration {

    @Bean
    public OAuth2FeignRequestInterceptor oAuth2FeignRequestInterceptor(ReactiveOAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        return new OAuth2FeignRequestInterceptor(oAuth2AuthorizedClientService);
    }
}
