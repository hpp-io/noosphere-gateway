package io.hpp.noosphere.gw.web.rest.vm;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class AgentDTO implements Serializable {

  private UUID id;

  private String name;

  private String walletAddress;

  private StatusCode statusCode;

  private String description;


}
