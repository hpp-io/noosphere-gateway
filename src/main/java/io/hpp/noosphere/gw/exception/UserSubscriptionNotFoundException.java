package io.hpp.noosphere.gw.exception;

import io.hpp.noosphere.common.exception.NotFoundException;
import io.hpp.noosphere.gw.config.Constants;

public class UserSubscriptionNotFoundException extends NotFoundException {

    public UserSubscriptionNotFoundException(String value) {
        super(Constants.PROPERTY_NAME_CONTAINER, value);
    }
}
