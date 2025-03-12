package se.loge.bwcontrol.mpk.hardware.ifc;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIHasHost {
  public default ControllerHost host() {
    return ExtensionStore.getStore().getHost();
  }

  public default Transport transport() {
    return ExtensionStore.getStore().getTransport();
  }

  public default CursorTrack primaryTrack() {
    return ExtensionStore.getStore().getPrimaryTrackCursor();
  }

  public default CursorDevice primaryDevice() {
    return ExtensionStore.getStore().getPrimaryDeviceCursor();
  }

  public default CursorDevice primaryInstrument() {
    return ExtensionStore.getStore().getPrimaryInstrumentCursor();
  }

  public default void println(String s) {
    host().println(s);
  }
}
