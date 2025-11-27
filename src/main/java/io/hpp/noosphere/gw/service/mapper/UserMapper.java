package io.hpp.noosphere.gw.service.mapper;

import io.hpp.noosphere.gw.domain.Authority;
import io.hpp.noosphere.gw.domain.User;
import io.hpp.noosphere.gw.service.dto.UserDTO;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link User} and its DTO called {@link UserDTO}.
 * <p>
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

  public List<UserDTO> usersToUserDTOs(List<User> users) {
    return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).toList();
  }

  public UserDTO userToUserDTO(User user) {
    if (user == null) {
      return null;
    } else {
      UserDTO userDto = new UserDTO();
      userDto.setId(user.getId());
      userDto.setName(user.getName());
      userDto.setEmail(user.getEmail());
      userDto.setImageUrl(user.getImageUrl());
      userDto.setApiKey(user.getApiKey());
      userDto.setWalletAddress(user.getWalletAddress());
      userDto.setLangKey(user.getLangKey());
      userDto.setActivated(user.isActivated());
      Set<String> authorities = this.getAuthorities(user);
      userDto.setAuthorities(authorities);
      return userDto;
    }
  }

  public List<User> userDTOsToUsers(List<UserDTO> userDTOs) {
    return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).toList();
  }


  public Set<String> getAuthorities(User user) {
    return user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
  }

  public User userDTOToUser(UserDTO userDTO) {
    if (userDTO == null) {
      return null;
    } else {
      User user = new User();
      user.setId(userDTO.getId());
      user.setName(userDTO.getName());
      user.setEmail(userDTO.getEmail());
      user.setLogin(userDTO.getLogin());
      user.setImageUrl(userDTO.getImageUrl());
      user.setApiKey(userDTO.getApiKey());
      user.setWalletAddress(userDTO.getWalletAddress());
      if (Boolean.TRUE.equals(userDTO.getActivated())) {
        user.setActivated(true);
      } else {
        user.setActivated(false);
      }
      user.setLangKey(userDTO.getLangKey());
      Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
      user.setAuthorities(authorities);
      return user;
    }
  }

  public Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
    Set<Authority> authorities = new HashSet<>();

    if (authoritiesAsString != null) {
      authorities = authoritiesAsString.stream().map(string -> {
        Authority auth = new Authority();
        auth.setName(string);
        return auth;
      }).collect(Collectors.toSet());
    }

    return authorities;
  }

  public User userFromId(String id) {
    if (id == null) {
      return null;
    }
    User user = new User();
    user.setId(id);
    return user;
  }

  @Named("id")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  public UserDTO toDtoId(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDto = new UserDTO();
    userDto.setId(user.getId());
    return userDto;
  }

  @Named("idSet")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  public Set<UserDTO> toDtoIdSet(Set<User> users) {
    if (users == null) {
      return Collections.emptySet();
    }

    Set<UserDTO> userSet = new HashSet<>();
    for (User userEntity : users) {
      userSet.add(this.toDtoId(userEntity));
    }

    return userSet;
  }

  @Named("email")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "email", source = "email")
  public UserDTO toDtoEmail(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDto = new UserDTO();
    userDto.setId(user.getId());
    userDto.setEmail(user.getEmail());
    return userDto;
  }

  @Named("emailSet")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "email", source = "email")
  public Set<UserDTO> toDtoEmailSet(Set<User> users) {
    if (users == null) {
      return Collections.emptySet();
    }

    Set<UserDTO> userSet = new HashSet<>();
    for (User userEntity : users) {
      userSet.add(this.toDtoEmail(userEntity));
    }

    return userSet;
  }
}
