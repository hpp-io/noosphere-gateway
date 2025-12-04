package io.hpp.noosphere.gw.service;

import static io.hpp.noosphere.common.config.Constants.PROPERTY_NAME_EMAIL;
import static io.hpp.noosphere.common.config.Constants.PROPERTY_NAME_LOCALE;
import static io.hpp.noosphere.common.config.Constants.PROPERTY_NAME_NAME;
import static io.hpp.noosphere.common.config.Constants.SYSTEM;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_API_KEY;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_IMAGE_URL;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_LANG_KEY;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_WALLET_ADDRESS;

import io.hpp.noosphere.common.service.KeycloakService;
import io.hpp.noosphere.common.service.util.CommonUtils;
import io.hpp.noosphere.gw.client.NoosphereHubClient;
import io.hpp.noosphere.gw.config.Constants;
import io.hpp.noosphere.gw.domain.Authority;
import io.hpp.noosphere.gw.domain.User;
import io.hpp.noosphere.gw.repository.AuthorityRepository;
import io.hpp.noosphere.gw.repository.UserRepository;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import io.hpp.noosphere.gw.service.mapper.UserMapper;
import io.hpp.noosphere.gw.web.rest.vm.UpdateWalletVm;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final AuthorityRepository authorityRepository;

  private final CacheManager cacheManager;
  private final EntityManager entityManager;
  private final UserMapper userMapper;

  private final KeycloakService keycloakService;
  private final NoosphereHubClient noosphereHubClient;

  public UserService(
    UserRepository userRepository,
    AuthorityRepository authorityRepository,
    CacheManager cacheManager,
    UserMapper userMapper,
    KeycloakService keycloakService,
    NoosphereHubClient noosphereHubClient,
    EntityManager entityManager
  ) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.cacheManager = cacheManager;
    this.userMapper = userMapper;
    this.keycloakService = keycloakService;
    this.noosphereHubClient = noosphereHubClient;
    this.entityManager = entityManager;
  }

  private static User getUser(Map<String, Object> details) {
    User user = new User();
    Boolean activated = Boolean.TRUE;
    String sub = String.valueOf(details.get("sub"));
    String username = null;
    if (details.get("preferred_username") != null) {
      username = ((String) details.get("preferred_username")).toLowerCase();
    }
    // handle resource server JWT, where sub claim is email and uid is ID
    if (details.get("uid") != null) {
      user.setId((String) details.get("uid"));
      user.setLogin(sub);
    } else {
      user.setId(sub);
    }
    if (username != null) {
      user.setLogin(username);
    } else if (user.getLogin() == null) {
      user.setLogin(user.getId());
    }
    if (details.get("given_name") != null) {
      user.setFirstName((String) details.get("given_name"));
    } else if (details.get(PROPERTY_NAME_NAME) != null) {
      user.setFirstName((String) details.get(PROPERTY_NAME_NAME));
    }
    if (details.get("family_name") != null) {
      user.setLastName((String) details.get("family_name"));
    }
    if (details.get("email_verified") != null) {
      activated = (Boolean) details.get("email_verified");
    }
    if (details.get(PROPERTY_NAME_EMAIL) != null) {
      user.setEmail(((String) details.get(PROPERTY_NAME_EMAIL)).toLowerCase());
    } else if (sub.contains("|") && (username != null && username.contains("@"))) {
      // special handling for Auth0
      user.setEmail(username);
    } else {
      user.setEmail(sub);
    }
    if (details.get(PROPERTY_NAME_LANG_KEY) != null) {
      user.setLangKey((String) details.get(PROPERTY_NAME_LANG_KEY));
    } else if (details.get(PROPERTY_NAME_LOCALE) != null) {
      // trim off country code if it exists
      String locale = (String) details.get(PROPERTY_NAME_LOCALE);
      if (locale.contains("_")) {
        locale = locale.substring(0, locale.indexOf('_'));
      } else if (locale.contains("-")) {
        locale = locale.substring(0, locale.indexOf('-'));
      }
      user.setLangKey(locale.toLowerCase());
    } else {
      // set langKey to default if not specified by IdP
      user.setLangKey(Constants.DEFAULT_LANGUAGE);
    }
    if (details.get("picture") != null) {
      user.setImageUrl((String) details.get("picture"));
    }
    if (details.get(PROPERTY_NAME_API_KEY) != null) {
      user.setApiKey((String) details.get(PROPERTY_NAME_API_KEY));
    }
    if (details.get(PROPERTY_NAME_WALLET_ADDRESS) != null) {
      user.setWalletAddress((String) details.get(PROPERTY_NAME_WALLET_ADDRESS));
    }
    user.setActivated(activated);
    return user;
  }

  public void createUser(UserDTO userDTO) {
    User user = userMapper.userDTOToUser(userDTO);
    userRepository.save(user);
    this.clearUserCaches(user);
    LOG.debug("Created User: {}", user);
  }

  public void updateUser(
    String userId,
    String firstName,
    String lastName,
    String email,
    String apiKey,
    String langKey,
    String imageUrl,
    String walletAddress
  ) {
    userRepository.findById(userId)
      .ifPresent(user -> {
        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (CommonUtils.isValid(email)) {
          user.setEmail(email.trim().toLowerCase());
        }
        if (CommonUtils.isValid(apiKey)) {
          user.setApiKey(apiKey.trim());
        }

        user.setLangKey(langKey);
        user.setImageUrl(imageUrl);

        if (CommonUtils.isValid(walletAddress)) {
          user.setWalletAddress(walletAddress.trim());
        }

        userRepository.save(user);
        this.clearUserCaches(user);
        LOG.debug("Changed Information for User: {}", user);
      });
  }

  @Transactional(readOnly = true)
  public List<String> getAuthorities() {
    return authorityRepository.findAll().stream().map(Authority::getName).toList();
  }

  public Mono<Void> updateUserProfile(String userId, String firstName, String lastName, String langKey, String imageUrl, Instant timestamp) {
    if (!CommonUtils.isValid(userId)) {
      return Mono.empty();
    }

    return Mono
      .fromRunnable(() ->
        userRepository
          .findById(userId)
          .ifPresent(user -> {
            if (CommonUtils.isValid(firstName)) {
              user.setFirstName(firstName.trim());
            }
            if (CommonUtils.isValid(lastName)) {
              user.setLastName(lastName.trim());
            }
            if (CommonUtils.isValid(firstName) || CommonUtils.isValid(lastName)) {
              user.setName(CommonUtils.buildFullName(langKey, firstName, lastName));
            }
            if (CommonUtils.isValid(langKey)) {
              user.setLangKey(langKey.trim());
            }
            if (CommonUtils.isValid(imageUrl)) {
              user.setImageUrl(imageUrl.trim());
            }
            user.setLastModifiedDate(timestamp);
            noosphereHubClient.updateUserProfile(userMapper.userToUserDTO(user));
            userRepository.save(user);
            clearUserCaches(user);
          })
      )
      .subscribeOn(Schedulers.boundedElastic())
      .then();
  }

  private User syncUserWithIdP(Map<String, Object> details, User user) {
    // save authorities in to sync user roles/groups between IdP and JHipster's local database
    Collection<String> dbAuthorities = getAuthorities();
    Collection<String> userAuthorities = user.getAuthorities().stream().map(Authority::getName).toList();
    for (String authority : userAuthorities) {
      if (!dbAuthorities.contains(authority)) {
        LOG.debug("Saving authority '{}' in local database", authority);
        Authority authorityToSave = new Authority();
        authorityToSave.setName(authority);
        authorityRepository.save(authorityToSave);
      }
    }
    // save account in to sync users between IdP and JHipster's local database
    Optional<User> existingUserOptional = userRepository.findOneByEmail(user.getEmail());
    existingUserOptional.ifPresentOrElse(
      existingUser -> {
        // if IdP sends last updated information, use it to determine if an update should happen
        if (details.get("updated_at") != null) {
          Instant dbModifiedDate = existingUser.getLastModifiedDate();
          Instant idpModifiedDate;
          if (details.get("updated_at") instanceof Instant) {
            idpModifiedDate = (Instant) details.get("updated_at");
          } else {
            idpModifiedDate = Instant.ofEpochSecond((Integer) details.get("updated_at"));
          }
          if (idpModifiedDate.isAfter(dbModifiedDate)) {
            LOG.debug("Updating user '{}' in local database", user.getLogin());
            updateUser(
              user.getId(),
              user.getFirstName(),
              user.getLastName(),
              user.getEmail(),
              user.getApiKey(),
              user.getLangKey(),
              user.getImageUrl(),
              user.getWalletAddress()
            );
            userRepository.save(user);
            this.clearUserCaches(user);
          }
          // no last updated info, blindly update
        } else {
          LOG.debug("Updating user '{}' in local database", user.getLogin());
          updateUser(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getApiKey(),
            user.getLangKey(),
            user.getImageUrl(),
            user.getWalletAddress()
          );
          userRepository.save(user);
          this.clearUserCaches(user);
        }
      },
      () -> {
        LOG.debug("Saving user '{}' in local database", user.getLogin());
        if (!CommonUtils.isValid(user.getCreatedBy())) {
          user.setCreatedBy(SYSTEM);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
      }
    );
    return user;
  }

  /**
   * Returns the user from an OAuth 2.0 login or resource server with JWT. Synchronizes the user in the local repository.
   *
   * @param authToken the authentication token.
   * @return the user from the authentication.
   */
  public UserDTO getUserFromAuthentication(AbstractAuthenticationToken authToken) {
    Map<String, Object> attributes;
    if (authToken instanceof OAuth2AuthenticationToken) {
      attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
    } else if (authToken instanceof JwtAuthenticationToken) {
      attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
    } else {
      throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
    }
    User user = getUser(attributes);
    user.setAuthorities(
      authToken
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .map(authority -> {
          Authority auth = new Authority();
          auth.setName(authority);
          return auth;
        })
        .collect(Collectors.toSet())
    );

    return new UserDTO(syncUserWithIdP(attributes, user));
  }

  private void clearUserCaches(String email, String apiKey) {
    Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evictIfPresent(email);
    if (CommonUtils.isValid(apiKey)) {
      Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_API_KEY_CACHE)).evictIfPresent(apiKey);
    }
  }

  private void clearUserCaches(User user) {
    this.clearUserCaches(user.getEmail(), user.getApiKey());
  }

  @Transactional(readOnly = true)
  public Page<UserDTO> search(String name, Boolean activated, Pageable pageable) {
    LOG.debug("Request to search Users");
    return userRepository.search(name, activated, pageable).map(userMapper::userToUserDTO);
  }

  @Transactional(readOnly = true)
  public Optional<User> findOptionalEntityById(String userId) {
    return userRepository.findById(userId);
  }

  @Transactional(readOnly = true)
  public User findEntityById(String userId) {
    Optional<User> optionalUser = this.findOptionalEntityById(userId);
    return optionalUser.orElseThrow();
  }

  public UserDTO findById(String userId) {
    return userMapper.userToUserDTO(this.findEntityById(userId));
  }

  public UserDTO createUserFromKeycloakUser(UserRepresentation keycloakUser) {
    UserDTO userDTO = null;
    if (keycloakUser != null) {
      userDTO = new UserDTO();
      userDTO.setId(keycloakUser.getId());
      userDTO.setLogin(keycloakUser.getUsername());
      userDTO.setLangKey(keycloakService.getAttributeValue(keycloakUser, PROPERTY_NAME_LANG_KEY));
      userDTO.setFirstName(keycloakUser.getFirstName());
      userDTO.setLastName(keycloakUser.getLastName());
      userDTO.setName(CommonUtils.buildFullName(userDTO.getLangKey(), keycloakUser.getFirstName(), keycloakUser.getLastName()));
      userDTO.setEmail(keycloakUser.getEmail());
      userDTO.setImageUrl(keycloakService.getAttributeValue(keycloakUser, PROPERTY_NAME_IMAGE_URL));
      userDTO.setApiKey(keycloakService.getAttributeValue(keycloakUser, PROPERTY_NAME_API_KEY));
      userDTO.setWalletAddress(keycloakService.getAttributeValue(keycloakUser, PROPERTY_NAME_WALLET_ADDRESS));
      userDTO.setActivated(keycloakUser.isEnabled());
      keycloakService.populateAuthoritiesFromKeycloakUser(userDTO);
    }
    return userDTO;
  }

  public UserDTO createKeycloakUser(UserDTO userDTO) {
    UserRepresentation userRepresentation = keycloakService.createKeycloakUser(userDTO);
    userDTO = this.createUserFromKeycloakUser(userRepresentation);
    this.createUser(userDTO);
    entityManager.flush();
    this.clearUserCaches(userDTO.getEmail(), userDTO.getApiKey());
    return userDTO;
  }

  public void updateMyWalletAddress(String userId, String walletAddress, Instant timestamp) {
    if (CommonUtils.isValid(userId) && CommonUtils.isValid(walletAddress)) {
      this.findOptionalEntityById(userId).ifPresent(user -> {
        user.setWalletAddress(walletAddress);
        user.setLastModifiedDate(timestamp);
//        keycloakService.updateKeycloakUser(user.getId(), user.getEmail(), null, null, null, null, null, null, walletAddress);
        userRepository.save(user);
        this.clearUserCaches(user);
      });
    }
  }

  public void updateMyApiKey(String userId, String apiKey, Instant timestamp) {
    if (CommonUtils.isValid(userId) && CommonUtils.isValid(apiKey)) {
      this.findOptionalEntityById(userId).ifPresent(user -> {
        user.setApiKey(apiKey);
        user.setLastModifiedDate(timestamp);
//        keycloakService.updateKeycloakUser(user.getId(), user.getEmail(), null, null, null, apiKey, null, null, null);
        userRepository.save(user);
        this.clearUserCaches(user);
      });
    }
  }

  public Mono<String> updateWithNewMyWallet(String userId, String ownerAddress, Instant timestamp) {
    UpdateWalletVm updateWalletVm = new UpdateWalletVm();
    updateWalletVm.setOwnerAddress(ownerAddress);
    return noosphereHubClient
      .createMyWallet(updateWalletVm)
      .publishOn(Schedulers.boundedElastic())
      .doOnNext(walletAddress -> {
        if (CommonUtils.isValid(walletAddress)) {
          this.updateMyWalletAddress(userId, walletAddress, timestamp);
        } else {
          throw new IllegalStateException("Failed to extract wallet address from receipt for user ID: " + userId);
        }
      });
  }

  public Mono<String> createAndUpdateMyWallet(String userId, String ownerAddress, Instant timestamp) {
    //    UserDTO userDTO = this.findById(userId);
    //    if (CommonUtils.isValid(userDTO.getWalletAddress())) {
    //      throw new InvalidDataException(PROPERTY_NAME_USER, "wallet exists");
    //    }
    return this.updateWithNewMyWallet(userId, ownerAddress, timestamp);
  }

  public Mono<String> updateWithNewMyApiKey(String userId, Instant timestamp) {
    return noosphereHubClient
      .createMyApiKey()
      .publishOn(Schedulers.boundedElastic())
      .doOnNext(newApiKey -> {
        if (CommonUtils.isValid(newApiKey)) {
          this.updateMyApiKey(userId, newApiKey, timestamp);
        } else {
          throw new IllegalStateException("Failed to create api key for user ID: " + userId);
        }
      });
  }

  public Mono<String> createAndUpdateMyApiKey(String userId, Instant timestamp) {
    return this.updateWithNewMyApiKey(userId, timestamp);
  }
}
