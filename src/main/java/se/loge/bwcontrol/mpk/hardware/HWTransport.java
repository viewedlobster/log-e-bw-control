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

  public final int MIDI_TRANSPORT_CHANNEL = 0;

  public final int MIDI_TRANSPORT_CC_RWD  = 115;
  public final int MIDI_TRANSPORT_CC_FFWD = 116;
  public final int MIDI_TRANSPORT_CC_STOP = 117;
  public final int MIDI_TRANSPORT_CC_PLAY = 118;
  public final int MIDI_TRANSPORT_CC_REC  = 119;

  public final int MIDI_TRANSPORT_VAL_ON_PRESS = 127;

  public HWTransport(HardwareSurface hwsurface) {
    rwd = hwsurface.createHardwareButton("transport_rwd");
    ffwd = hwsurface.createHardwareButton("transport_ffwd");
    stop = hwsurface.createHardwareButton("transport_stop");
    play = hwsurface.createHardwareButton("transport_play");
    rec = hwsurface.createHardwareButton("transport_rec");
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... rest) {
    rwd.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC_RWD, MIDI_TRANSPORT_VAL_ON_PRESS));
    ffwd.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC_FFWD, MIDI_TRANSPORT_VAL_ON_PRESS));
    stop.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC_STOP, MIDI_TRANSPORT_VAL_ON_PRESS));
    play.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC_PLAY, MIDI_TRANSPORT_VAL_ON_PRESS));
    rec.pressedAction().setActionMatcher(midiIn.createCCActionMatcher(
      MIDI_TRANSPORT_CHANNEL, MIDI_TRANSPORT_CC_REC, MIDI_TRANSPORT_VAL_ON_PRESS));
  }
}
