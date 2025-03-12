package se.loge.bwcontrol.common;

import java.util.LinkedList;
import java.util.List;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.common.CallbackRegistry.MatchingCallback;

public class MPKStore extends ExtensionStore {
  private ControllerHost host;
  private Transport transport;
  private CursorTrack primaryTrackCursor;
  private CursorDevice primaryDeviceCursor;
  private CursorDevice primaryInstrumentCursor;
  private MidiIn midi0;
  private CallbackRegistry<ShortMidiMessage> midi0Callback;
  private CallbackRegistry<String> sysex0Callback;
  
  static final String MPK_PRIMARY_CURSOR_NAME = "Primary";
  static final String MPK_PRIMARY_INSTRUMENT_NAME = "Primary Instrument";

  private MPKStore(ControllerHost h) {
    midi0Callback = new CallbackRegistry<>();
    sysex0Callback = new CallbackRegistry<>();
    host = h;
  }

  public static ExtensionStore initStore(ControllerHost host)
  {
    assert(store == null);
    store = new MPKStore(host);
    return store;
  }

  @Override
  public CallbackPair<CallbackRegistry<ShortMidiMessage>, CallbackRegistry<String>> registerMidiIn(MidiIn midiIn) {
    assert(midi0 == null);
    midi0 = midiIn;
    return new CallbackPair<>(midi0Callback, sysex0Callback);
  }

  @Override
  public void registerMidiCallback(MidiIn midiIn, MatchingCallback<ShortMidiMessage> cb) {
    assert(midi0 != null && midiIn == midi0);
    midi0Callback.register(cb);
  }

  @Override
  public void registerSysexCallback(MidiIn midiIn, MatchingCallback<String> cb) {
    assert(midi0 != null && midiIn == midi0);
    sysex0Callback.register(cb);
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

  @Override
  public CursorTrack getPrimaryTrackCursor() {
    if (primaryTrackCursor == null) {
      primaryTrackCursor = host.createCursorTrack("mpk_primary_track", MPK_PRIMARY_CURSOR_NAME, 0, 0, true);
    }
    return primaryTrackCursor;
  }

  @Override
  public CursorDevice getPrimaryDeviceCursor() {
    if (primaryDeviceCursor == null) {
       primaryDeviceCursor = getPrimaryTrackCursor().createCursorDevice("mpk_primary_device", MPK_PRIMARY_CURSOR_NAME, 0, CursorDeviceFollowMode.FOLLOW_SELECTION);
    }
    return primaryDeviceCursor;
  }

  @Override
  public CursorDevice getPrimaryInstrumentCursor() {
    if (primaryInstrumentCursor == null) {
      primaryInstrumentCursor = getPrimaryTrackCursor().createCursorDevice(
        "mpk_primary_instrument", MPK_PRIMARY_INSTRUMENT_NAME, 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
    }
    return primaryInstrumentCursor;
  }
}