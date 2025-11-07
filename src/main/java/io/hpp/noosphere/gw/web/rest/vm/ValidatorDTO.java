package io.hpp.noosphere.gw.web.rest.vm;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class ValidatorDTO implements Serializable {

    private UUID id;

    private String name;

    private String walletAddress;
    private String verifierAddress;
    private String imageName;
    private Integer port;
    private Integer command;
    private Integer environmentVariables;
    private Integer volumes;
    private Integer payments;


    private StatusCode statusCode;


}
