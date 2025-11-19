package io.hpp.noosphere.gw.web.rest.vm;

import lombok.Data;

@Data
public class RegisterAgentVm {

    private String name;

    private String apiKey;

    private String walletAddress;

    private String email;
}
