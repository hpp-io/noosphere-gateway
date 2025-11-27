package io.hpp.noosphere.gw.exception;

import io.hpp.noosphere.common.exception.NotFoundException;
import io.hpp.noosphere.gw.config.Constants;

public class VerifierNotFoundException extends NotFoundException {

  public VerifierNotFoundException(String value) {
    super(Constants.PROPERTY_NAME_VERIFIER, value);
  }
}
