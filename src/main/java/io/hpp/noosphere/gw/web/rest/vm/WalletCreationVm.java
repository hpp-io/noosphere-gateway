package io.hpp.noosphere.gw.web.rest.vm;

import java.security.KeyStore;
import lombok.Data;

@Data
public class WalletCreationVm {

  private KeyStore keyStore;
  private CreateWalletVm createWalletVm;


  public WalletCreationVm(KeyStore keyStore, CreateWalletVm createWalletVm) {
    this.keyStore = keyStore;
    this.createWalletVm = createWalletVm;
  }

}