package io.hpp.noosphere.gw.web.rest.vm;

import java.security.KeyStore;
import lombok.Data;

@Data
public class WalletCreationVm {

  private KeyStore keyStore;
  private CreateWalletVm createWalletVm;
  private String walletAddress;


  public WalletCreationVm(KeyStore keyStore, CreateWalletVm createWalletVm, String walletAddress) {
    this.keyStore = keyStore;
    this.createWalletVm = createWalletVm;
    this.walletAddress = walletAddress;
  }

}