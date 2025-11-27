package io.hpp.noosphere.gw.exception;

import io.hpp.noosphere.common.exception.NotFoundException;
import io.hpp.noosphere.gw.config.Constants;

public class AgentRequestNotFoundException extends NotFoundException {

  public AgentRequestNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_AGENT_REQUEST, value);
  }
}
