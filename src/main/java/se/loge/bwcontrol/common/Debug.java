package se.loge.bwcontrol.common;

import com.bitwig.extension.controller.api.ControllerHost;

public interface Debug {
  default ControllerHost host() {
    return ExtensionStore.getStore().getHost();
  }
  default void println(String s) {
    host().println(s);
  }
}
