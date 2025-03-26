package se.loge.bwcontrol.mpk.hardware.ifc;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIStateAccess<S> {
  public default S controllerState() {
    return (S)ExtensionStore.getStore().extra();
  }
}
