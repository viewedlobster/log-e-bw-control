package se.loge.bwcontrol.common;

import java.util.LinkedList;
import java.util.Queue;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.ifc.HasOutputState;

public class BWHost {
  private static ControllerHost host;
  private static HardwareSurface surface;
  private static Transport transport;
  private static CursorTrack primaryTrackCursor;
  private static CursorDevice primaryDeviceCursor;
  private static CursorDevice primaryInstrumentCursor;
  private static Queue<HasOutputState> needsUpdate;

  private static LogLevel logLevel;

  public static final String PRIMARY_CURSOR_NAME = "Primary";
  public static final String PRIMARY_INSTRUMENT_NAME = "Primary Instrument";
  public static final int PRIMARY_CURSOR_TRACK_NUM_SENDS = 0;
  public static final int PRIMARY_CURSOR_TRACK_NUM_SCENES = 8;
  public static final boolean PRIMARY_CURSOR_TRACK_FOLLOW = true;

  public static enum LogLevel {
    DEBUG(10),
    INFO(20),
    WARNING(30),
    ERROR(40);

    private final int lvl;

    private LogLevel(int lvl) {
      this.lvl = lvl;
    }

    public boolean leq(LogLevel other) {
      return (this.lvl <= other.lvl);
    }
  }

  public static void setup(ControllerHost h) {
    setup(h, LogLevel.INFO);
  }

  public static void setup(ControllerHost h, LogLevel lvl) {
    host = h;
    logLevel = lvl;
    surface = host.createHardwareSurface();
    transport = host.createTransport();
    primaryTrackCursor = host.createCursorTrack(
      "mpk_primary_track", PRIMARY_CURSOR_NAME,
      PRIMARY_CURSOR_TRACK_NUM_SENDS,
      PRIMARY_CURSOR_TRACK_NUM_SCENES, 
      PRIMARY_CURSOR_TRACK_FOLLOW);
    primaryDeviceCursor = primaryTrackCursor.createCursorDevice(
      "mpk_primary_device", PRIMARY_CURSOR_NAME, 0,
      CursorDeviceFollowMode.FOLLOW_SELECTION);
    primaryInstrumentCursor = primaryTrackCursor.createCursorDevice(
      "mpk_primary_instrument", PRIMARY_INSTRUMENT_NAME, 0,
      CursorDeviceFollowMode.FIRST_INSTRUMENT);

    needsUpdate = new LinkedList<>();
  }
  public static ControllerHost host() {
    return host;
  }
  public static HardwareSurface surface() {
    return surface;
  }
  public static Transport transport() {
    return transport;
  }
  public static CursorTrack primaryTrack() {
    return primaryTrackCursor;
  }
  public static CursorDevice primaryDevice() {
    return primaryDeviceCursor;
  }
  public static CursorDevice primaryInstrument() {
    return primaryInstrumentCursor;
  }

  public static void signalHardwareUpdate(HasOutputState elm) {
    needsUpdate.add(elm);
  }
  public static boolean shouldUpdateHardware() {
    return (! needsUpdate.isEmpty());
  }
  public static void updateHardware() {
    while (! needsUpdate.isEmpty()) {
      needsUpdate.poll().onHardwareUpdate();
    }
  }

  public static void logln(LogLevel lvl, String fmt, Object... objs) {
    if (logLevel.leq(lvl)) {
      switch (lvl) {
        case DEBUG:
        case INFO:
          host.println(lvl+":"+String.format(fmt, objs));
          break;
        case WARNING:
        case ERROR:
          host.errorln(lvl+":"+String.format(fmt, objs));
          break;
      }
    }
  }
  public static void debugln(String fmt, Object... objs) {
    logln(LogLevel.DEBUG, fmt, objs);
  }
  public static void errorln(String fmt, Object... objs) {
    logln(LogLevel.ERROR, fmt, objs);
  }

  public static HardwareActionBindable customAction(Runnable r, String descr) {
    return host.createAction(r, () -> descr);
  }

  public static HardwareActionBindable customAction(Runnable r) {
    return host.createAction(r, () -> { return "log-e-bw-control internal action"; });
  }
}
