package io.hpp.noosphere.gw.web.rest.dto;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class AgentRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;


  private AgentDTO agent;
  private ContainerDTO container;
  private UserSubscriptionDTO userSubscription;


  private StatusCode statusCode;


  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("agent", agent != null ? agent.getId() : null)
      .append("container", container != null ? container.getId() : null)
      .append("userSubscription", userSubscription != null ? userSubscription.getId() : null)
      .append("statusCode", statusCode)
      .toString();
  }
}
