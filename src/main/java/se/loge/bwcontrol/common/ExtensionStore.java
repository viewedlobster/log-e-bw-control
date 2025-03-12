package se.loge.bwcontrol.common;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.Transport;

public abstract class ExtensionStore {
  protected static ExtensionStore store;

  public static ExtensionStore getStore() {
    assert(store != null);
    return store;
  }

  public abstract ControllerHost getHost();
  public abstract Transport getTransport();
  public abstract CursorTrack getPrimaryTrackCursor();
  public abstract CursorDevice getPrimaryDeviceCursor();
  public abstract CursorDevice getPrimaryInstrumentCursor();
  public abstract CallbackPair<CallbackRegistry<ShortMidiMessage>, CallbackRegistry<String>> registerMidiIn(MidiIn midiIn);
  public abstract void registerMidiCallback(MidiIn midiIn, CallbackRegistry.MatchingCallback<ShortMidiMessage> cb);
  public abstract void registerSysexCallback(MidiIn midiIn, CallbackRegistry.MatchingCallback<String> cb);

}
