package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.service.UserService;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api")
public class AccountResource {

  private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);
  private final UserService userService;

  public AccountResource(UserService userService) {
    this.userService = userService;
  }

  /**
   * {@code GET  /account} : get the current user.
   *
   * @return the current user.
   * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
   */
  @GetMapping("/account")
  @RateLimited
  public Mono<UserDTO> getAccount() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(authentication -> {
            if (authentication instanceof AbstractAuthenticationToken) {
                return Mono.fromCallable(() -> userService.getUserFromAuthentication((AbstractAuthenticationToken) authentication))
                    .subscribeOn(Schedulers.boundedElastic());
            }
            return Mono.error(new AccountResourceException("User could not be found"));
        })
        .switchIfEmpty(Mono.error(new AccountResourceException("User could not be found")));
  }

  /**
   * {@code GET  /authenticate} : check if the user is authenticated.
   *
   * @return the {@link Mono} with status {@code 204 (No Content)} if the user is authenticated, or with status {@code 401 (Unauthorized)} if not.
   */
  @GetMapping("/authenticate")
  @RateLimited
  public Mono<ResponseEntity<Void>> isAuthenticated() {
    LOG.debug("REST request to check if the current user is authenticated");
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(authentication -> ResponseEntity.noContent().<Void>build())
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  private static class AccountResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private AccountResourceException(String message) {
      super(message);
    }
  }

}
