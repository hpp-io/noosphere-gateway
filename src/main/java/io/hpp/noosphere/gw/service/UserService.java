package io.hpp.noosphere.gw.service;

import static io.hpp.noosphere.common.config.Constants.SYSTEM;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_API_KEY;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_IMAGE_URL;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_LANG_KEY;
import static io.hpp.noosphere.gw.config.Constants.PROPERTY_NAME_WALLET_ADDRESS;

import io.hpp.noosphere.common.service.KeycloakService;
import io.hpp.noosphere.gw.config.Constants;
import io.hpp.noosphere.gw.domain.Authority;
import io.hpp.noosphere.gw.domain.User;
import io.hpp.noosphere.gw.repository.AuthorityRepository;
import io.hpp.noosphere.gw.repository.UserRepository;
import io.hpp.noosphere.gw.security.SecurityUtils;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import io.hpp.noosphere.gw.service.mapper.UserMapper;
import io.hpp.noosphere.common.service.util.CommonUtils;
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

  public UserService(
    UserRepository userRepository,
    AuthorityRepository authorityRepository,
    CacheManager cacheManager,
    UserMapper userMapper,
    KeycloakService keycloakService,
    EntityManager entityManager
  ) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.cacheManager = cacheManager;
    this.userMapper = userMapper;
    this.keycloakService = keycloakService;
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
    } else if (details.get("name") != null) {
      user.setFirstName((String) details.get("name"));
    }
    if (details.get("family_name") != null) {
      user.setLastName((String) details.get("family_name"));
    }
    if (details.get("email_verified") != null) {
      activated = (Boolean) details.get("email_verified");
    }
    if (details.get("email") != null) {
      user.setEmail(((String) details.get("email")).toLowerCase());
    } else if (sub.contains("|") && (username != null && username.contains("@"))) {
      // special handling for Auth0
      user.setEmail(username);
    } else {
      user.setEmail(sub);
    }
    if (details.get("langKey") != null) {
      user.setLangKey((String) details.get("langKey"));
    } else if (details.get("locale") != null) {
      // trim off country code if it exists
      String locale = (String) details.get("locale");
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
    if (details.get("api_key") != null) {
      user.setApiKey((String) details.get("api_key"));
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

  /**
   * Update basic information (first name, last name, email, language) for the current user.
   *
   * @param firstName first name of user.
   * @param lastName  last name of user.
   * @param email     email id of user.
   * @param langKey   language key.
   * @param imageUrl  image URL of user.
   */
  @Transactional
  public void updateUser(
    String firstName,
    String lastName,
    String email,
    String apiKey,
    String langKey,
    String imageUrl,
    String walletAddress
  ) {
    Optional<String> currentUserLogin = Optional.ofNullable(SecurityUtils.getCurrentUserLogin().block());
    if (currentUserLogin.isEmpty()) {
      return;
    }
    Optional<User> optionalUser = userRepository.findOneByEmail(currentUserLogin.get());
    if (optionalUser.isEmpty()) {
      return;
    }
    User user = optionalUser.get();
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
    keycloakService.updateKeycloakUser(
      user.getId(),
      user.getEmail(),
      firstName,
      lastName,
      email,
      apiKey,
      langKey,
      imageUrl,
      walletAddress
    );
    userRepository.save(user);
    this.clearUserCaches(user);
    LOG.debug("Changed Information for User: {}", user);
  }

  @Transactional(readOnly = true)
  public List<String> getAuthorities() {
    return authorityRepository.findAll().stream().map(Authority::getName).toList();
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
    Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
    if (existingUser.isPresent()) {
      // if IdP sends last updated information, use it to determine if an update should happen
      if (details.get("updated_at") != null) {
        Instant dbModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
        Instant idpModifiedDate;
        if (details.get("updated_at") instanceof Instant) {
          idpModifiedDate = (Instant) details.get("updated_at");
        } else {
          idpModifiedDate = Instant.ofEpochSecond((Integer) details.get("updated_at"));
        }
        if (idpModifiedDate.isAfter(dbModifiedDate)) {
          LOG.debug("Updating user '{}' in local database", user.getLogin());
          updateUser(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getApiKey(),
            user.getLangKey(),
            user.getImageUrl(),
            user.getWalletAddress()
          );
        }
        // no last updated info, blindly update
      } else {
        LOG.debug("Updating user '{}' in local database", user.getLogin());
        updateUser(
          user.getFirstName(),
          user.getLastName(),
          user.getEmail(),
          user.getApiKey(),
          user.getLangKey(),
          user.getImageUrl(),
          user.getWalletAddress()
        );
      }
    } else {
      LOG.debug("Saving user '{}' in local database", user.getLogin());
      if (!CommonUtils.isValid(user.getCreatedBy())){
        user.setCreatedBy(SYSTEM);
      }
      userRepository.save(user);
      this.clearUserCaches(user);
    }
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
    return optionalUser.orElse(null);
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
}
