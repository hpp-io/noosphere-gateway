package io.hpp.noosphere.gw.web.rest;

import io.hpp.noosphere.gw.config.RateLimited;
import io.hpp.noosphere.gw.service.UserService;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
   * @param principal the current user; resolves to {@code null} if not authenticated.
   * @return the current user.
   * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
   */
  @GetMapping("/account")
  @RateLimited
  public Mono<UserDTO> getAccount(Principal principal) {
    if (principal instanceof AbstractAuthenticationToken) {
      return Mono.just(userService.getUserFromAuthentication((AbstractAuthenticationToken) principal));
    } else {
      throw new AccountResourceException("User could not be found");
    }
  }

  /**
   * {@code GET  /authenticate} : check if the user is authenticated.
   *
   * @return the {@link ResponseEntity} with status {@code 204 (No Content)}, or with status {@code 401 (Unauthorized)} if not authenticated.
   */
  @GetMapping("/authenticate")
  public ResponseEntity<Void> isAuthenticated(Principal principal) {
    LOG.debug("REST request to check if the current user is authenticated");
    return ResponseEntity.status(principal == null ? HttpStatus.UNAUTHORIZED : HttpStatus.NO_CONTENT).build();
  }

  private static class AccountResourceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private AccountResourceException(String message) {
      super(message);
    }
  }

}
