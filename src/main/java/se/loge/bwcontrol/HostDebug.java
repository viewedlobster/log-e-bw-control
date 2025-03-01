package se.loge.bwcontrol;

import com.bitwig.extension.controller.api.ControllerHost;

public abstract class HostDebug {
  static ControllerHost host;

  public static void setHost(ControllerHost h) {
    host = h;
  }

  public static void println(String s) {
    host.println(s);
  }
}
