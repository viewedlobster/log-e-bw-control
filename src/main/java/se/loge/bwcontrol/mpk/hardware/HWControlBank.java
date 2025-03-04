package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

public class HWControlBank implements HWIMidiIn, HWIMidiOut {
  public final int MPK261_NUM_CONTROL_STRIPS = 8;
  
  final HardwareButton[] S;
  final AbsoluteHardwareKnob[] K;
  final HardwareSlider[] F;

  String id;

  /*
   * control bank A: instrument: macros, filter 
   * control bank B: daw tracks; volume, arm, mute?, solo?
   * control bank C: global controls and control mode
   */

  public HWControlBank(HardwareSurface hwsurface, String bankId) {
    S = new HardwareButton[MPK261_NUM_CONTROL_STRIPS];
    K = new AbsoluteHardwareKnob[MPK261_NUM_CONTROL_STRIPS];
    F = new HardwareSlider[MPK261_NUM_CONTROL_STRIPS];
    id = bankId;

    for ( int i = 0; i < MPK261_NUM_CONTROL_STRIPS; i++ ) {
      S[i] = hwsurface.createHardwareButton(String.format("cbank_%s_S_%d", id, i));
      K[i] = hwsurface.createAbsoluteHardwareKnob(String.format("cbank_%s_K_%d", id, i));
      F[i] = hwsurface.createHardwareSlider(String.format("cbank_%s_F_%d", id, i));
    }

    // TODO: add the lights
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    // TODO implement
  }

  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    // TODO implement
  }


}
