package se.loge.bwcontrol.mpk.hardware.ifc;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWISignalsHardwareUpdate {
  public default void signalHardwareUpdate(int type) {
    ExtensionStore.getStore().signalHardwareUpdate(type);
  }
}
