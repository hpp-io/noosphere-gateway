package io.hpp.noosphere.gw.web.rest.vm.search;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import lombok.Data;

@Data
public class SearchVerifierVm {

    private String name;
    private String searchText;
    private String walletAddress;
    private String verifierAddress;
    private StatusCode statusCode;
    private String createdByUserId;
}
