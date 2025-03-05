package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIControlCC;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;

import com.bitwig.extension.controller.api.HardwareButton;

public class HWDawControl implements HWIMidiIn, HWIControlCC {
  final HardwareButton enter;
  final HardwareButton up;
  final HardwareButton down;
  final HardwareButton left;
  final HardwareButton right;

  // TODO replace values with proper ones from hardware
  final static int MIDI_ENTER = 0x1;
  final static int MIDI_UP    = 0x2;
  final static int MIDI_DOWN  = 0x3;
  final static int MIDI_LEFT  = 0x4;
  final static int MIDI_RIGHT = 0x5;

  public HWDawControl(HardwareSurface hwsurface) {
    enter = hwsurface.createHardwareButton("dawc_enter");
    up = hwsurface.createHardwareButton("dawc_up");
    down = hwsurface.createHardwareButton("dawc_down");
    left = hwsurface.createHardwareButton("dawc_left");
    right = hwsurface.createHardwareButton("dawc_right");

  }


  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    // TODO: implement
  }

  public void bindCCActions() {
    // TODO implement
  }

}
