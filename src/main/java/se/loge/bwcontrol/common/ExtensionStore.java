package se.loge.bwcontrol.common;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;

public abstract class ExtensionStore {
  protected static ExtensionStore store;

  public static ExtensionStore getStore() {
    assert(store != null);
    return store;
  }

  public abstract ControllerHost getHost();
  public abstract Transport getTransport();

  // TODO get current active device
  // TODO set macro mappings
  // TODO set synth mappings
}
