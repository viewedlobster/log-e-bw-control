package se.loge.bwcontrol.mpk.hardware.ifc;

import se.loge.bwcontrol.mpk.MPKHost;
import se.loge.bwcontrol.mpk.state.MPKState;

public interface HWIMPKStateAccess {
  public default MPKState state() {
    return MPKHost.state();
  }
}
