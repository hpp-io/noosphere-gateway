package io.hpp.noosphere.gw.web.rest.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private String firstName;
  private String lastName;
  private String email;
  private String imageUrl;
  private String apiKey;
  private String walletAddress;
  private String langKey;


}
