package se.loge.bwcontrol.mpk.hardware.ifc;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIHasOutputState {
  public default void registerHardwareUpdateCallback(int id, Runnable f) {
    ExtensionStore.getStore().registerHardwareUpdateCallback(id, f);
  }
}
