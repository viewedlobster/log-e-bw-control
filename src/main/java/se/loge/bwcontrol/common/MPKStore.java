package se.loge.bwcontrol.common;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;

public class MPKStore extends ExtensionStore {
  private ControllerHost host;
  private Transport transport;

  private MPKStore(ControllerHost h) {
    host = h;
  }

  public static ExtensionStore initStore(ControllerHost host) {
    assert(store == null);
    store = new MPKStore(host);
    return store;
  }

  @Override
  public ControllerHost getHost() {
    return host;
  }

  @Override
  public Transport getTransport() {
    if (transport == null) {
      transport = host.createTransport();
    }
    return transport;
  }
}
