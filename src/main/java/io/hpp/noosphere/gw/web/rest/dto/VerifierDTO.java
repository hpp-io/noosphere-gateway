package io.hpp.noosphere.gw.web.rest.dto;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class VerifierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String name;

    private String walletAddress;
    private String verifierAddress;
    private String imageName;
    private Integer port;
    private String command;
    private String environmentVariables;
    private String volumes;
    private String payments;


    private StatusCode statusCode;


}
