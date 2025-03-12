package se.loge.bwcontrol.mpk.hardware.ifc;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;

import se.loge.bwcontrol.common.CallbackRegistry;
import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIUsingCallbacks {
  public default void registerSysexCallback(MidiIn midi, CallbackRegistry.MatchingCallback<String> cb) {
    ExtensionStore.getStore().registerSysexCallback(midi, cb);
  }
  public default void registerMidiCallback(MidiIn midi, CallbackRegistry.MatchingCallback<ShortMidiMessage> cb) {
    ExtensionStore.getStore().registerMidiCallback(midi, cb);
  }
}
