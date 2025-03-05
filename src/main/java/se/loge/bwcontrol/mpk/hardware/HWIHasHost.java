package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIHasHost {
  public default ControllerHost host() {
    return ExtensionStore.getStore().getHost();
  }

  public default Transport transport() {
    return ExtensionStore.getStore().getTransport();
  }
}
