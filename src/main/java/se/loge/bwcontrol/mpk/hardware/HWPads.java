package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.NoteInput;

public class HWPads implements HWIMidiIn, HWIMidiOut {
  static final String MPK_PADS_NOTE_INPUT_ID = "mpk_pads";
  static final String[] MPK_PAD_BANK_NAMES = {
    "A",
    "B",
    "C",
    "D",
  };
  static final int MPK_PAD_BANK_SIZE = 16;

  static final int MPK_PADS_MIDI_CHANNEL = 9;

  NoteInput noteIn;
  private final HWPadBank[] padBanks;

  public HWPads(HardwareSurface hwsurface) {

    padBanks = new HWPadBank[MPK_PAD_BANK_NAMES.length];
    for (int i = 0; i < MPK_PAD_BANK_NAMES.length; i++) {
      padBanks[i] = new HWPadBank(hwsurface, MPK_PAD_BANK_NAMES[i],
        new NoteRange(i*MPK_PAD_BANK_SIZE, MPK_PAD_BANK_SIZE));
    }
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < padBanks.length; i++) {
      padBanks[i].connectMidiIn(midiIn, midiIns);
    }
    noteIn = midiIn.createNoteInput(MPK_PADS_NOTE_INPUT_ID,
      String.format("8%x????", MPK_PADS_MIDI_CHANNEL), // note off
      String.format("9%x????", MPK_PADS_MIDI_CHANNEL), // note on
      String.format("a%x????", MPK_PADS_MIDI_CHANNEL), // poly aftertouch
      String.format("d%x????", MPK_PADS_MIDI_CHANNEL) // mono aftertouch
      );
  }

  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    // TODO: implement
  }



}
