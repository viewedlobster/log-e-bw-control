package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.MidiIn;

public interface HWIMidiIn {
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns);
}
