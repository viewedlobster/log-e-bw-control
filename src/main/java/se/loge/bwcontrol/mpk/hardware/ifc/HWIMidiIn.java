package se.loge.bwcontrol.mpk.hardware.ifc;

import com.bitwig.extension.controller.api.MidiIn;

public interface HWIMidiIn {
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns);
}
