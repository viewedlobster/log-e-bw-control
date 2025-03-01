package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.HardwareButton;

public class HWTransport implements HWIMidiIn {
  final HardwareButton play;
  final HardwareButton stop;
  final HardwareButton rec;
  final HardwareButton ffwd;
  final HardwareButton rwd;

  private final int MIDI_TRANSPORT_CHANNEL = 0x00;
  public final int MIDI_TRANSPORT_PLAY = 0x1;
  public final int MIDI_TRANSPORT_STOP = 0x2;
  public final int MIDI_TRANSPORT_REC  = 0x3;
  public final int MIDI_TRANSPORT_FFWD = 0x4;
  public final int MIDI_TRANSPORT_RWD  = 0x5;

  public final int MIDI_TRANSPORT_CC = 102;
  public final int MIDI_TRANSPORT_CCV_STOP = 0;
  public final int MIDI_TRANSPORT_CCV_PLAY = 1;
  public final int MIDI_TRANSPORT_CCV_FFWD = 2;
  public final int MIDI_TRANSPORT_CCV_RWD  = 3;
  public final int MIDI_TRANSPORT_CCV_REC  = 4;

  public HWTransport(HardwareSurface hwsurface) {
    play = hwsurface.createHardwareButton("transport_play");
    stop = hwsurface.createHardwareButton("transport_stop");
    rec = hwsurface.createHardwareButton("transport_rec");
    ffwd = hwsurface.createHardwareButton("transport_ffwd");
    rwd = hwsurface.createHardwareButton("transport_rwd");

    
    // TODO initialize buttons
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... rest) {
    stop.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC, MIDI_TRANSPORT_CCV_STOP));
    play.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC, MIDI_TRANSPORT_CCV_PLAY));
    ffwd.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC, MIDI_TRANSPORT_CCV_FFWD));
    rwd.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC, MIDI_TRANSPORT_CCV_RWD));
    rec.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC, MIDI_TRANSPORT_CCV_REC));
  }

}
