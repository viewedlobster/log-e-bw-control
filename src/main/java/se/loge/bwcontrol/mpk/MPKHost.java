package se.loge.bwcontrol.mpk;

import com.bitwig.extension.controller.api.ControllerHost;

import se.loge.bwcontrol.common.BWHost;
import se.loge.bwcontrol.mpk.state.MPKState;

public class MPKHost extends BWHost {

  public static MPKState state() {
    return mpkState;
  }

  public static void setup(ControllerHost h) {
    setup(h, LogLevel.INFO);
  }

  public static void setup(ControllerHost h, LogLevel lvl) {
    BWHost.setup(h, lvl);

    mpkState = new MPKState();
  }

  public static void init() {
    mpkState.init();
  }

  private static MPKState mpkState;
}
