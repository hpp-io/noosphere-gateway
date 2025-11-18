package io.hpp.noosphere.gw.web.rest.dto;

import lombok.Data;

@Data
public class KeystoreReadRequest {

    private String keyAlias;
    private Boolean isWallet;
    private Boolean isHppWallet;
    private String password;
    private String fileContent;
}
