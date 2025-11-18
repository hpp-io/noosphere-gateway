package io.hpp.noosphere.gw.web.rest.dto;

import lombok.Data;

@Data
public class KeystoreRequest {

    private String keyAlias;
    private Boolean isWallet;
    private Boolean createHppWallet;
    private String password;
    private String privateKey;
}
