package se.loge.bwcontrol.mpk.hardware.ifc;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIHasOutputState {
  public void onHardwareUpdate();
  default public void signalHardwareUpdate() {
    ExtensionStore.getStore().signalHardwareUpdate(this);
  }
}
