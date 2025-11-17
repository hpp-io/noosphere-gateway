package io.hpp.noosphere.gw.web.rest.dto;

import lombok.Data;

@Data
public class KeystoreRequest {

    private String keyAlias;
    private String password;
    private String privateKey;
}
