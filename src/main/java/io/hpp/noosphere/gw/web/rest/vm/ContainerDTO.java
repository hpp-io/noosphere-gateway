package io.hpp.noosphere.gw.web.rest.vm;

import io.hpp.noosphere.gw.web.rest.vm.enumeration.StatusCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@SuppressWarnings("common-java:DuplicatedBlocks")
@Data
public class ContainerDTO implements Serializable {

    private UUID id;

    private String name;

    private String walletAddress;

    private BigDecimal price;

    private StatusCode statusCode;

    private String description;

    private String parameters;


}
