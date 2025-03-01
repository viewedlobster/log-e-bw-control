package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareControl;

public class HWPadBank implements HWIMidiIn {

  final static int MPK261_NUM_PADS = 16;
  final static int MPK_PADS_MIDI_CHANNEL = 0;

  final HardwareButton[] pads;
  final String id;
  final NoteRange range;
  NoteInput noteIn;

  public HWPadBank(HardwareSurface hwsurface, String bankId, NoteRange r) {
    pads = new HardwareButton[MPK261_NUM_PADS];
    id = bankId;
    range = r;

    for ( int i = 0; i < MPK261_NUM_PADS; i++ ) {
      pads[i] = hwsurface.createHardwareButton(String.format("pad_%s_%d",id, i));
    }

    // TODO initialize pads
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < pads.length; i++) {
      pads[i].pressedAction().setActionMatcher(
        midiIn.createNoteOnActionMatcher(MPK_PADS_MIDI_CHANNEL,
          range.getNote(i)));

      pads[i].releasedAction().setActionMatcher(
        midiIn.createNoteOffActionMatcher(MPK_PADS_MIDI_CHANNEL,
          range.getNote(i)));
    }
  }

}
