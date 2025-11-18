package io.hpp.noosphere.gw.web.rest.dto;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.PeriodType;
import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class UserSubscriptionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;

  private BigDecimal amount;

  private PeriodType periodType;

  private Integer periodValue;

  private StatusCode statusCode;

  private UserDTO owner;

  private ContainerDTO container;


  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("id", id)
      .append("amount", amount)
      .append("periodType", periodType)
      .append("periodValue", periodValue)
      .append("statusCode", statusCode)
      .append("owner", owner != null ? owner.getId() : null)
      .append("container", container != null ? container.getId() : null)
      .toString();
  }
}
