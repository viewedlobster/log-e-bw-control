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

  public static final String PRIMARY_CURSOR_NAME = "Primary";
  public static final String PRIMARY_INSTRUMENT_NAME = "Primary Instrument";
  public static final int PRIMARY_CURSOR_TRACK_NUM_SENDS = 0;
  public static final int PRIMARY_CURSOR_TRACK_NUM_SCENES = 8;
  public static final boolean PRIMARY_CURSOR_TRACK_FOLLOW = true;


  public static void setup(ControllerHost h) {
    host = h;

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

  public static void println(String fmt, Object... objs) {
    host.println(String.format(fmt, objs));
  }
  public static void errorln(String fmt, Object... objs) {
    host.errorln(String.format(fmt, objs));
  }

  public static HardwareActionBindable customAction(Runnable r, String descr) {
    return host.createAction(r, () -> descr);
  }

  public static HardwareActionBindable customAction(Runnable r) {
    return host.createAction(r, () -> { return "log-e-bw-control internal action"; });
  }
}
