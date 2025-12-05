package io.hpp.noosphere.gw.web.rest.vm;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema
public class CreateWalletVm {

    @Schema(description = "Owner Address", requiredMode = RequiredMode.REQUIRED)
    private String ownerAddress;

}
