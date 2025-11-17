package io.hpp.noosphere.gw.web.rest.dto;

import lombok.Data;

@Data
public class KeystoreValidationRequest {

    private String keyAlias;
    private Boolean isWallet;
    private String password;
    private String fileContent;
}
